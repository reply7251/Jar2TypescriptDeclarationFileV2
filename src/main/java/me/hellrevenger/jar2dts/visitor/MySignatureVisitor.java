package me.hellrevenger.jar2dts.visitor;

import java.util.List;

public interface MySignatureVisitor {
    default MySignatureVisitor visitGenerics(List<String> generics) {
        return this;
    }
    default MySignatureVisitor visitSuperclass(String superclass) {
        return this;
    }

    default MySignatureVisitor visitReturnType(String type) {
        return this;
    }

    default MySignatureVisitor visitParameter(String type) {
        return this;
    }
    default MySignatureVisitor visitInterface(String superclass) {
        return this;
    }

    default MySignatureVisitor visitEnd() { return this; }
}
