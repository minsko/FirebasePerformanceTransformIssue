package com.example.transform
import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform.QualifiedContent.Scope
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import org.apache.commons.io.FileUtils

/**
* Prints out some information and copies inputs to outputs.
*/
public class MyTransform extends Transform {

    String name
    boolean printCopyInfo
    
    public MyTransform(String name) {
        this(name, false)
    }
    public MyTransform(String name, boolean printCopyInfo) {
        this.name = name
        this.printCopyInfo = printCopyInfo
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<ContentType> getInputTypes() {
        return ImmutableSet.<ContentType>of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    public Set<Scope> getScopes() {
        return Sets.immutableEnumSet(Scope.PROJECT, Scope.PROJECT_LOCAL_DEPS, Scope.EXTERNAL_LIBRARIES)
    }


    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs,
            Collection<TransformInput> referencedInputs,
            TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        Map<String, Integer> nameMap = new HashMap<String, Integer>()
        println "\n***********START($name)*************"
        printTransformInputs(inputs)
        for (TransformInput input: inputs)
        {
            for (DirectoryInput dirInput: input.directoryInputs) {
                File dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                if (printCopyInfo) {
                    println "Copying ${dirInput.name} to ${dest.absolutePath}"
                }
                FileUtils.copyDirectory(dirInput.getFile(), dest)
            }

            for (JarInput jarInput: input.jarInputs) {
                def src = jarInput.getFile()
                File dest = outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (printCopyInfo) {
                    println "Copying ${jarInput.name} to ${dest.absolutePath}"
                }
                FileUtils.copyFile(jarInput.getFile(), dest)
            }
        }
        println "\n************END($name)**************\n"
    }

    private void print(String s) {
        System.out.println(s)
    }

    private void printTransformInputs(Collection<TransformInput> inputs) {
        println "Inputs :"
        for (TransformInput input: inputs) {
            printTransformInput(input)
        }
    }

    private void printTransformInput(TransformInput input) {
        Collection<DirectoryInput> dirInputs = input.getDirectoryInputs();
        int i=0;
        println "Dir Inputs :"
        for (DirectoryInput dirInput: dirInputs) {
            printQualifiedContent(dirInput, false, i++)
        }
        if (i == 0) {
            println "No Directory Inputs"
        }
        i=0;
        println "Jar Inputs :"
        Collection<JarInput> jarInputs = input.getJarInputs();
        for (JarInput jarInput: jarInputs) {
            printQualifiedContent(jarInput, true, i++)
        }
        if (i == 0) {
            println "No Jar Inputs"
        }
    }

    private void printQualifiedContent(QualifiedContent content, boolean isJar, int num) {
        println "${isJar?"Jar":"Directory"} ${num} ${content.name} : ${content.file.absolutePath}"
    }

}
