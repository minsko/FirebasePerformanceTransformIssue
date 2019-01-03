# Firebase Performance Transform Issue
Demonstration of an issue with the Firebase Performance Transform included in the firebase-perf plugin.

Inside the app's build.gradle you control easily enable/disable the BeforeFirebase and AfterFirebase transforms as well as the Firebase-pref plugin.

The Firebase transform corrupts the names of the inputs so they cannot be identified by later transforms.

Instead of simply passing on the unique original name provided by the TransformAPI, it generates new name from an MD5 hash of the full file path of the input, appending it with a dash and the name of the file (without an extension).

So, for example, an input originally named `com.android.support:design:26.1.0` located at `C:\Users\minsko.PREINT\.gradle\caches\transforms-1\files-1.1\design-26.1.0.aar\7c6f530e863897457f23e211cda29700\jars\classes.jar` is output using a new name `886900b45843611bb2b14b56155b84e3-classes`

This sample has transforms which simply print out the names and pass the contents along.

`FirebaseEnabledOutput.txt` contains the output from a `gradlew assembleDebug` with no modifications to `app/build.gradle`.
You can see the corrupted names in the `AfterFirebase` transform.

`FirebaseDisableOutput.txt` contains the output from a `gradlew assembleDebug` with the Firebase Plugin disabled in `app/build.gradle`.
You can see the original names used by the `AfterFirebase` transform, as they have not been corrupted.

`FirebaseFirstOutput.txt` contains the output from a `gradlew assembleDebug` with the `BeforeFirebase` transform disabled in `app/build.gradle`.
You can see the corrupted names in the `AfterFirebase` transform.

Before the Firebase transform the inputs have unique names like:

 * android.local.jars:ClassA.jar:c15a27ee0eb37bd83a4dab187a8da5b93864719d
 * com.android.support:design:26.1.0
 * com.android.support:appcompat-v7:26.1.0
 * android.arch.lifecycle:common:1.0.0
 * android.arch.core:common:1.0.0 
 
After the Firebase transform the inputs have names like:

 * 4e9f562149faa90a240352ae42573e9d-ClassA
 * 886900b45843611bb2b14b56155b84e3-classes
 * a1bc9fbafd893b444116d6080283ed49-classes
 * d05a5db790a6d26b08d5b157f0328c64-common-1.0.0
 * df6e4f2fcc7a300b86c9b82dc0894556-common-1.0.0

These new names are not helpful to later transforms when determining the context of the original dependency.
