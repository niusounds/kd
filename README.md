# kd

## build native (JVM)

```shell
mkdir build
cd build
cmake ../kd/src/jvmMain
msbuild kd_jni.sln -P:Configuration=Release
cp ./build/Release/opus_jni.dll ../kd/src/jvmMain/resources/opus_jni.dll
cp ./build/Release/wdl.dll ../kd/src/jvmMain/resources/wdl.dll
```
