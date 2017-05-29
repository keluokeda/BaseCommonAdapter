package com.keluokeda;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private static final String VIEW_HOLDER = "ViewHolder";

    private static final ClassName CLASSNAME_VIEWHOLDER = ClassName.get("com.keluokeda.basecommomadapter", "BaseViewHolder");
    private static final ClassName CLASSNAME_VALUEBINDER = ClassName.get("com.keluokeda.basecommomadapter", "ValueBinder");
    private static final ClassName CLASSNAME_BASECOMMONADAPTER = ClassName.get("com.keluokeda.basecommomadapter", "BaseCommonAdapter");
    private static final ClassName CLASSNAME_VIEW = ClassName.get("android.view", "View");

    private Elements mElements;
    private Filer mFiler;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //执行此方法时，会把一个类 中所有元素的注解给抽取出来（包括类注解，方法注解，变量注解)
        try {

            Set<? extends Element> methodSets = roundEnvironment.getElementsAnnotatedWith(Bind.class);


            //class set 个数永远是1
            Set<? extends Element> clazzSets = roundEnvironment.getElementsAnnotatedWith(Item.class);

            if (clazzSets.size() != 1) {
                return true;
            }

            if (methodSets.isEmpty()) {
                error("bean class with %s should has %s method", Item.class.getSimpleName(), Bind.class.getSimpleName());
                return true;
            }

            String packageName = null;
            String itemClassSimpleName = null;
            ClassName itemClassName = null;
            int layoutId = 0;
            for (Element element : clazzSets) {
                packageName = mElements.getPackageOf(element).getQualifiedName().toString();
                itemClassSimpleName = element.getSimpleName().toString();
                itemClassName = ClassName.get((TypeElement) element);
                Item item = element.getAnnotation(Item.class);
                layoutId = item.resource();
            }


            MethodSpec.Builder bindMethodSpecBuilder = MethodSpec.methodBuilder("bindData")
                    .addAnnotation(Override.class)
                    .addParameter(itemClassName, "item")
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addParameter(CLASSNAME_VIEW, "view");
            String viewHolderClassName = itemClassSimpleName + "_" + VIEW_HOLDER;


            TypeSpec.Builder viewHolderBuilder = TypeSpec.classBuilder(itemClassSimpleName + "_" + VIEW_HOLDER)
                    .addSuperinterface(ParameterizedTypeName.get(CLASSNAME_VIEWHOLDER, itemClassName));

            //循环遍历 每个带有注解的方法
            for (Element element : methodSets) {


                ExecutableElement executableElement = (ExecutableElement) element;
                String itemMethodName = executableElement.getSimpleName().toString();


                TypeMirror resultTypeMirror = executableElement.getReturnType();
                TypeName resultClassName = ClassName.get(resultTypeMirror);


                Bind bind = element.getAnnotation(Bind.class);

                ClassName viewClassName = ClassName.bestGuess(bind.viewClassName());
                int id = bind.targetId();
                String viewFieldName = itemMethodName + "_" + viewClassName.simpleName();

                ClassName binderClassName = ClassName.bestGuess(bind.binderClassName());
                String binderFieldName = itemMethodName + "_" + binderClassName.simpleName();
                FieldSpec binderFieldSpec = FieldSpec.builder(binderClassName, binderFieldName, Modifier.PRIVATE).build();
                viewHolderBuilder.addField(binderFieldSpec);


                constructorBuilder.addStatement("this.$L = ($L) view.findViewById($L)", viewFieldName, viewClassName.simpleName(), id)
                        .addStatement("this.$L = new $T()", binderFieldName, binderClassName);


                FieldSpec viewFieldSpec = FieldSpec.builder(viewClassName, viewFieldName, Modifier.PRIVATE).build();
                viewHolderBuilder.addField(viewFieldSpec);


                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(CLASSNAME_VALUEBINDER, viewClassName, resultClassName);
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(itemMethodName)
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(parameterizedTypeName, "binder")
                        .addParameter(viewClassName, "view")
                        .addParameter(resultClassName, "value")
                        .addStatement("binder.setValue(view,value)");

                viewHolderBuilder.addMethod(methodSpecBuilder.build());


                bindMethodSpecBuilder.addStatement("$L( $L , $L ,item.$L() )", itemMethodName, binderFieldName, viewFieldName, itemMethodName);


            }


            viewHolderBuilder.addMethod(constructorBuilder.build())
                    .addMethod(bindMethodSpecBuilder.build());

            JavaFile.builder(packageName, viewHolderBuilder.build()).build().writeTo(mFiler);


            MethodSpec createViewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
                    .returns(ParameterizedTypeName.get(CLASSNAME_VIEWHOLDER, itemClassName))
                    .addParameter(CLASSNAME_VIEW, "view")
                    .addModifiers(Modifier.PROTECTED)
                    .addAnnotation(Override.class)
                    .addStatement("return new $T(view)", ClassName.bestGuess(viewHolderClassName))
                    .build();

            MethodSpec adapterConstructorMethodSpec = MethodSpec.constructorBuilder()
                    .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), itemClassName), "list")
                    .addStatement("super(list)").build();

            MethodSpec getResourceMethodSpec = MethodSpec.methodBuilder("getItemResource")
                    .returns(TypeName.INT)
                    .addStatement("return $L", layoutId)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PROTECTED)
                    .build();

            TypeSpec adapterTypeSpec = TypeSpec.classBuilder(itemClassSimpleName + "_Adapter")
                    .addMethod(adapterConstructorMethodSpec)
                    .addMethod(getResourceMethodSpec)
                    .addMethod(createViewHolderMethodSpec)
                    .superclass(ParameterizedTypeName.get(CLASSNAME_BASECOMMONADAPTER, itemClassName))
                    .build();

            JavaFile.builder(packageName, adapterTypeSpec).build().writeTo(mFiler);


        } catch (Exception e) {
            error(e.getMessage());
        }


        return true;
    }

    //给开发者提供错误信息
    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> strings = new LinkedHashSet<>(1);
        strings.add(Bind.class.getCanonicalName());
        strings.add(Item.class.getCanonicalName());
        return strings;
    }
}
