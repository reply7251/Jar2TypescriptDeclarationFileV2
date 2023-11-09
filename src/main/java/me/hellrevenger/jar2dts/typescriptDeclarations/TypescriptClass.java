package me.hellrevenger.jar2dts.typescriptDeclarations;

import me.hellrevenger.jar2dts.converter.AcceptTypeScriptData;
import me.hellrevenger.jar2dts.converter.TypeScriptData;

import java.util.ArrayList;
import java.util.List;

public class TypescriptClass extends Interface implements AcceptTypeScriptData {
    List<String> processedInterfaceMethods = new ArrayList<>();

    @Override
    public void handleSuperClass(TypeScriptData data) {
        if(!superClass.isEmpty()) {
            data.stringBuilder.append(" extends ");
            data.stringBuilder.append(superClass);
        }
        if(!interfaces.isEmpty()) {
            data.stringBuilder.append(" implements ");
            data.stringBuilder.append(String.join(", ", interfaces));
        }
    }

    public void processInterfaces(TypeScriptData data) {
        for(var interfaceName : interfaces) {
            interfaceName = data.namespacePrefix + interfaceName;
            if(interfaceName.contains("<")) interfaceName = interfaceName.split("<")[0];
            String location = interfaceName.substring(0,interfaceName.contains(".") ? interfaceName.lastIndexOf(".") : 0);
            String simpleName = interfaceName.substring(interfaceName.contains(".") ? interfaceName.lastIndexOf(".")+1 : 0);
            //System.out.printf("test: %s, %s, %s\n", location, simpleName, data.hasNamespace(location));
            if(!data.hasNamespace(location)) continue;
            var namespace = data.getNamespace(location);
            if(!namespace.hasInterface(simpleName)) continue;
            var interf = namespace.getInterface(simpleName);

            processInterface(data, interf);
        }
        if(!superClass.isEmpty()) {
            var interfaceName = data.namespacePrefix + superClass;
            if(interfaceName.contains("<")) interfaceName = interfaceName.split("<")[0];
            String location = interfaceName.substring(0,interfaceName.contains(".") ? interfaceName.lastIndexOf(".") : 0);
            String simpleName = interfaceName.substring(interfaceName.contains(".") ? interfaceName.lastIndexOf(".")+1 : 0);
            //System.out.printf("test: %s, %s, %s\n", location, simpleName, data.hasNamespace(location));
            if(!data.hasNamespace(location)) return;
            var namespace = data.getNamespace(location);
            if(!namespace.hasInterface(simpleName)) return;
            var interf = namespace.getInterface(simpleName);
            if(interf instanceof TypescriptClass tc) {
                tc.processInterfaces(data);
            }
        }
    }

    public void processInterface(TypeScriptData data, Interface interf) {
        for(var function : interf.functions.entrySet()) {
            //if(functions.containsKey(function.getKey()) || processedInterfaceMethods.contains(function.getKey())) continue;
            var currentFunc = functions.get(function.getKey());
            for(var func : function.getValue()) {
                if(processedInterfaceMethods.contains(func.getSign())) continue;
                if(currentFunc != null && currentFunc.stream().anyMatch(x -> x.getSign().equals(func.getSign()))) continue;

                func.accept(data);

                data.accessingStatic = true;
                func.accept(data);
                data.accessingOriginalName = true;
                func.accept(data);
                data.accessingStatic = false;
                func.accept(data);
                data.accessingOriginalName = false;
                processedInterfaceMethods.add(func.getSign());
            }
        }
    }

    @Override
    public void accept(TypeScriptData data) {
        if(name.isEmpty()) return;
        /*
        if(hasConstructor) {
            data.appendIndent();
            data.stringBuilder.append("interface ");
            data.stringBuilder.append(name);
            data.stringBuilder.append("$$constructor");

            handleGenerics(data);
            data.stringBuilder.append(" {\n");
            data.increaseIndent();

            data.accessingConstructor = true;

            for(var func : functions.values()) {
                for(var declaration : func)
                    declaration.accept(data);
            }

            data.accessingConstructor = false;

            data.decreaseIndent();
            data.appendIndent();
            data.stringBuilder.append("}\n");
        }

         */


        data.appendIndent();
        data.stringBuilder.append("class ");
        data.stringBuilder.append(name);
        //data.stringBuilder.append("Class");
        handleGenerics(data);

        handleSuperClass(data);

        data.stringBuilder.append(" {\n");
        data.increaseIndent();


        acceptMembers(data);

        processInterfaces(data);



        data.accessingStatic = true;
        acceptMembers(data);
        data.accessingOriginalName = true;
        acceptMembers(data);
        data.accessingStatic = false;
        acceptMembers(data);
        data.accessingOriginalName = false;

        if(hasConstructor) {
            data.accessingConstructor = true;
            for(var func : functions.values()) {
                for(var declaration : func)
                    declaration.accept(data);
            }
            data.accessingConstructor = false;
            data.appendIndent();
            data.stringBuilder.append("/** @deprecated */ constructor(...args: any[]) {}\n");
        }

        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");



        /*
        data.appendIndent();
        data.stringBuilder.append("interface ");
        data.stringBuilder.append(name);
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(generics);
            data.stringBuilder.append(">");
        }
        data.stringBuilder.append(" extends ");
        data.stringBuilder.append(name);
        //data.stringBuilder.append("Class");
        if(!generics.isEmpty()) {
            data.stringBuilder.append("<");
            data.stringBuilder.append(generics);
            data.stringBuilder.append(">");
        }
        data.stringBuilder.append(" {\n");
        data.increaseIndent();
        data.decreaseIndent();
        data.appendIndent();
        data.stringBuilder.append("}\n");

         */
        /*
        data.appendIndent();
        data.stringBuilder.append("declare var ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(": ");
        data.stringBuilder.append(name);
        data.stringBuilder.append(";\n");

         */
    }
}
