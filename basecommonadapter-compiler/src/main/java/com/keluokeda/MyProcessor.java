package com.keluokeda;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;


import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
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
    private static final ClassName CLASSNAME_ONCHILDVIEWCLICKLISTENER = ClassName.get("com.keluokeda.basecommomadapter", "OnChildViewClickListener");

    private static final String KEY_VIEWCLASS = "viewClass";
    private static final String KEY_BINDER_CLASS = "binderClass";
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
        //执行此方法的时候会把所有 含有目标注解的 元素 装在 roundEnvironment
        try {

            Set<? extends Element> methodSets = roundEnvironment.getElementsAnnotatedWith(Bind.class);


            Set<? extends Element> clazzSets = roundEnvironment.getElementsAnnotatedWith(Item.class);


            for (Element element : clazzSets) {


                String packageName = mElements.getPackageOf(element).getQualifiedName().toString();
                String itemClassSimpleName = element.getSimpleName().toString();
                ClassName itemClassName = ClassName.get((TypeElement) element);
                Item item = element.getAnnotation(Item.class);
                int layoutId = item.resource();

                String viewHolderClassName = itemClassSimpleName + "_" + VIEW_HOLDER;


                createViewHolderClass(methodSets, packageName, itemClassSimpleName, itemClassName, element);


                createAdapterClass(packageName, itemClassSimpleName, itemClassName, layoutId, viewHolderClassName);
            }


        } catch (Exception e) {
            e.printStackTrace();
            error("adapter processor has error %s", e);
        }


        return true;
    }

    private void createAdapterClass(String packageName, String itemClassSimpleName, ClassName itemClassName, int layoutId, String viewHolderClassName) throws IOException {
        MethodSpec createViewHolderMethodSpec = MethodSpec.methodBuilder("createViewHolder")
                .returns(ParameterizedTypeName.get(CLASSNAME_VIEWHOLDER, itemClassName))
                .addParameter(CLASSNAME_VIEW, "view")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addStatement("return new $T(view, this)", ClassName.bestGuess(viewHolderClassName))
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
    }

    private void createViewHolderClass(Set<? extends Element> methodSets, String packageName, String itemClassSimpleName, ClassName itemClassName, Element itemClassElement) throws IOException {
        Set<Element> elementSet = new LinkedHashSet<>();
        for (Element element : methodSets) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (typeElement == itemClassElement) {
                elementSet.add(element);
            }
        }

        if (elementSet.isEmpty()) {
            return;
        }

        FieldSpec itemFieldSpec = FieldSpec.builder(itemClassName, itemClassSimpleName.toLowerCase())
                .addModifiers(Modifier.PRIVATE).build();
        FieldSpec positionFieldSpec = FieldSpec.builder(TypeName.INT, "position")
                .addModifiers(Modifier.PRIVATE).build();

        FieldSpec listenerFieldSpec = FieldSpec.builder(CLASSNAME_ONCHILDVIEWCLICKLISTENER, "listener")
                .addModifiers(Modifier.PRIVATE).build();

        MethodSpec.Builder bindMethodSpecBuilder = MethodSpec.methodBuilder("bindData")
                .addAnnotation(Override.class)
                .addParameter(itemClassName, "item")
                .addParameter(TypeName.INT, "position")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.$L = item", itemClassSimpleName.toLowerCase())
                .addStatement("this.position = position");

        MethodSpec onclickMethodSpec = MethodSpec.methodBuilder("onClick")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CLASSNAME_VIEW, "view")
                .addStatement("this.listener.onChildViewClick( this.position , this.$L , view)", itemClassSimpleName.toLowerCase())
                .build();


        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addParameter(CLASSNAME_VIEW, "view")
                .addParameter(CLASSNAME_ONCHILDVIEWCLICKLISTENER, "listener")
                .addStatement("this.listener = listener");


        TypeSpec.Builder viewHolderBuilder = TypeSpec.classBuilder(itemClassSimpleName + "_" + VIEW_HOLDER)
                .addSuperinterface(ParameterizedTypeName.get(CLASSNAME_VIEWHOLDER, itemClassName));

        //循环遍历 每个带有注解的方法
        for (Element element : elementSet) {


            ExecutableElement executableElement = (ExecutableElement) element;
            String itemMethodName = executableElement.getSimpleName().toString();


            TypeMirror resultTypeMirror = executableElement.getReturnType();
            TypeName resultClassName = ClassName.get(resultTypeMirror);


            Bind bind = element.getAnnotation(Bind.class);
            ClassName viewClassName = null;
            ClassName binderClassName = null;

            //  这里不可以直接获取类信息 会出错
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                        annotationMirror.getElementValues().entrySet()) {
                    if (KEY_VIEWCLASS.equals(entry.getKey().getSimpleName().toString())) {
                        AnnotationValue value = entry.getValue();
                        TypeMirror typeMirror = (TypeMirror) value.getValue();
                        viewClassName = (ClassName) ClassName.get(typeMirror);
                    } else if (KEY_BINDER_CLASS.equals(entry.getKey().getSimpleName().toString())) {
                        AnnotationValue value = entry.getValue();
                        TypeMirror typeMirror = (TypeMirror) value.getValue();
                        binderClassName = (ClassName) ClassName.get(typeMirror);
                    }

                }
            }


            int id = bind.viewId();
            String viewFieldName = itemMethodName + "_" + viewClassName.simpleName();

            String binderFieldName = itemMethodName + "_" + binderClassName.simpleName();
            FieldSpec binderFieldSpec = FieldSpec.builder(binderClassName, binderFieldName, Modifier.PRIVATE).build();
            viewHolderBuilder.addField(binderFieldSpec);

            //初始化 成员变量
            constructorBuilder.addStatement("this.$L = ($L) view.findViewById($L)", viewFieldName, viewClassName.simpleName(), id)
                    .addStatement("this.$L = new $T()", binderFieldName, binderClassName);

            //设置 click 监听器
            if (bind.click()) {
                constructorBuilder.addStatement("this.$L.setOnClickListener(this)", viewFieldName);
            }


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


        viewHolderBuilder
                .addMethod(constructorBuilder.build())
                .addMethod(bindMethodSpecBuilder.build())
                .addMethod(onclickMethodSpec)
                .addField(itemFieldSpec)
                .addField(positionFieldSpec)
                .addField(listenerFieldSpec);

        JavaFile.builder(packageName, viewHolderBuilder.build()).build().writeTo(mFiler);
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

        Set<String> strings = new LinkedHashSet<>(2);
        strings.add(Bind.class.getCanonicalName());
        strings.add(Item.class.getCanonicalName());
        return strings;
    }
}
