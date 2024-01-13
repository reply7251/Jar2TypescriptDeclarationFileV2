package me.hellrevenger.jar2dts.visitor;

import me.hellrevenger.jar2dts.utils.ClassName;
import me.hellrevenger.jar2dts.utils.Scope;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySignatureReader {
    private final String signatureValue;
    String lastParsedType;

    /**
     * Constructs a {@link SignatureReader} for the given signature.
     *
     * @param signature A <i>JavaTypeSignature</i>, <i>ClassSignature</i> or <i>MethodSignature</i>.
     */

    public MySignatureReader(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    /**
     * Makes the given visitor visit the signature of this {@link SignatureReader}. This signature is
     * the one specified in the constructor (see {@link #SignatureReader}). This method is intended to
     * be called on a {@link SignatureReader} that was created using a <i>ClassSignature</i> (such as
     * the <code>signature</code> parameter of the {@link org.objectweb.asm.ClassVisitor#visit}
     * method) or a <i>MethodSignature</i> (such as the <code>signature</code> parameter of the {@link
     * org.objectweb.asm.ClassVisitor#visitMethod} method).
     *
     * @param signatureVistor the visitor that must visit this signature.
     */
    public void accept(MySignatureVisitor signatureVisitor) {
        String signature = this.signatureValue;
        var scope = signatureVisitor.getScope();
        int length = signature.length();
        int offset; // Current offset in the parsed signature (parsed from left to right).
        char currentChar; // The signature character at 'offset', or just before.

        // If the signature starts with '<', it starts with TypeParameters, i.e. a formal type parameter
        // identifier, followed by one or more pair ':',ReferenceTypeSignature (for its class bound and
        // interface bounds).
        if (signature.charAt(0) == '<') {
            // Invariant: offset points to the second character of a formal type parameter name at the
            // beginning of each iteration of the loop below.
            offset = 2;
            List<String> generics = new ArrayList<>();
            do {
                // The formal type parameter name is everything between offset - 1 and the first ':'.
                int classBoundStartOffset = signature.indexOf(':', offset);
                generics.add(ClassName.remap(signature.substring(offset - 1, classBoundStartOffset)));

                // If the character after the ':' class bound marker is not the start of a
                // ReferenceTypeSignature, it means the class bound is empty (which is a valid case).
                offset = classBoundStartOffset + 1;
                currentChar = signature.charAt(offset);
                if (currentChar == 'L' || currentChar == '[' || currentChar == 'T') {
                    offset = parseType(signature, offset, scope);
                }

                // While the character after the class bound or after the last parsed interface bound
                // is ':', we need to parse another interface bound.
                while ((currentChar = signature.charAt(offset++)) == ':') {
                    offset = parseType(signature, offset, scope);
                }

                // At this point a TypeParameter has been fully parsed, and we need to parse the next one
                // (note that currentChar is now the first character of the next TypeParameter, and that
                // offset points to the second character), unless the character just after this
                // TypeParameter signals the end of the TypeParameters.
            } while (currentChar != '>');
            signatureVisitor.visitGenerics(generics);
        } else {
            offset = 0;
        }

        // If the (optional) TypeParameters is followed by '(' this means we are parsing a
        // MethodSignature, which has JavaTypeSignature type inside parentheses, followed by a Result
        // type and optional ThrowsSignature types.
        if (signature.charAt(offset) == '(') {
            offset++;
            while (signature.charAt(offset) != ')') {
                offset = parseType(signature, offset, scope);
                signatureVisitor.visitParameter(lastParsedType);
            }
            // Use offset + 1 to skip ')'.
            offset = parseType(signature, offset + 1, scope);
            signatureVisitor.visitReturnType(lastParsedType);

            while (offset < length) {
                // Use offset + 1 to skip the first character of a ThrowsSignature, i.e. '^'.
                offset = parseType(signature, offset + 1, scope);
            }
        } else {
            // Otherwise we are parsing a ClassSignature (by hypothesis on the method input), which has
            // one or more ClassTypeSignature for the super class and the implemented interfaces.
            offset = parseType(signature, offset, scope);
            signatureVisitor.visitSuperclass(lastParsedType);
            //System.out.println("super: " + lastParsedType);
            while (offset < length) {
                offset = parseType(signature, offset, scope);
                signatureVisitor.visitInterface(lastParsedType);
                //System.out.println("super: " + lastParsedType);
            }
        }
        signatureVisitor.visitEnd();
    }

    /**
     * Parses a JavaTypeSignature and makes the given visitor visit it.
     *
     * @param signature a string containing the signature that must be parsed.
     * @param startOffset index of the first character of the signature to parsed.
     * @return the index of the first character after the parsed signature.
     */
    private int parseType(
            final String signature, final int startOffset, String scope) {
        int offset = startOffset; // Current offset in the parsed signature.
        char currentChar = signature.charAt(offset++); // The signature character at 'offset'.

        // Switch based on the first character of the JavaTypeSignature, which indicates its kind.
        switch (currentChar) {
            case 'Z':
            case 'C':
            case 'B':
            case 'S':
            case 'I':
            case 'F':
            case 'J':
            case 'D':
            case 'V':
                // Case of a BaseType or a VoidDescriptor.
                lastParsedType = parseBaseType(currentChar);
                return offset;

            case '[':
                // Case of an ArrayTypeSignature, a '[' followed by a JavaTypeSignature.
                offset = parseType(signature, offset, scope);
                lastParsedType += "[]";
                return offset;

            case 'T':
                // Case of TypeVariableSignature, an identifier between 'T' and ';'.
                int endOffset = signature.indexOf(';', offset);
                lastParsedType = signature.substring(offset, endOffset);
                return endOffset + 1;

            case 'L':

                // Case of a ClassTypeSignature, which ends with ';'.
                // These signatures have a main class type followed by zero or more inner class types
                // (separated by '.'). Each can have type arguments, inside '<' and '>'.
                int start = offset; // The start offset of the currently parsed main or inner class name.
                boolean visited = false; // Whether the currently parsed class name has been visited.
                boolean inner = false; // Whether we are currently parsing an inner class type.
                // Parses the signature, one character at a time.
                while (true) {
                    currentChar = signature.charAt(offset++);
                    if (currentChar == '.' || currentChar == ';') {
                        // If a '.' or ';' is encountered, this means we have fully parsed the main class name
                        // or an inner class name. This name may already have been visited it is was followed by
                        // type arguments between '<' and '>'. If not, we need to visit it here.
                        if (!visited) {
                            String name = Scope.reduceScope(scope, ClassName.remap(signature.substring(start, offset - 1)));
                            lastParsedType = name;
                        }
                        // If we reached the end of the ClassTypeSignature return, otherwise start the parsing
                        // of a new class name, which is necessarily an inner class name.
                        if (currentChar == ';') {
                            //signatureVisitor.visitEnd();
                            break;
                        }
                        start = offset;
                        visited = false;
                        inner = true;
                    } else if (currentChar == '<') {
                        // If a '<' is encountered, this means we have fully parsed the main class name or an
                        // inner class name, and that we now need to parse TypeArguments. First, we need to
                        // visit the parsed class name.
                        String name = Scope.reduceScope(scope, ClassName.remap(signature.substring(start, offset - 1)));
                        visited = true;
                        StringBuilder parsedType = new StringBuilder(name + "<");
                        // Now, parse the TypeArgument(s), one at a time.
                        while ((currentChar = signature.charAt(offset)) != '>') {
                            switch (currentChar) {
                                case '*':
                                    // Unbounded TypeArgument.
                                    ++offset;
                                    parsedType.append("any").append(",");
                                    break;
                                case '+':
                                case '-':
                                    // Extends or Super TypeArgument. Use offset + 1 to skip the '+' or '-'.
                                    offset =
                                            parseType(
                                                    signature, offset + 1, scope);
                                    parsedType.append(lastParsedType).append(",");
                                    break;
                                default:
                                    // Instanceof TypeArgument. The '=' is implicit.
                                    offset = parseType(signature, offset, scope);
                                    parsedType.append(lastParsedType).append(",");
                                    break;
                            }
                        }
                        lastParsedType = parsedType.toString();

                        if(!lastParsedType.endsWith("<")) {
                            lastParsedType = lastParsedType.substring(0,lastParsedType.length()-1)+">";
                        } else {
                            lastParsedType = lastParsedType.substring(0,lastParsedType.length()-1);
                        }
                    }
                }
                return offset;

            default:
                throw new IllegalArgumentException();
        }
    }

    public static String parseBaseType(char type) {
        return switch (type) {
            case 'Z' -> "boolean";
            case 'C' -> "char";
            case 'B' -> "byte";
            case 'S' -> "short";
            case 'I' -> "int";
            case 'F' -> "float";
            case 'J' -> "long";
            case 'D' -> "double";
            case 'V' -> "void";
            default -> null;
        };
    }
}
