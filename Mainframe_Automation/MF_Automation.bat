pushd %~dp0
set ProjectPath=%cd%
set classpath=%ProjectPath%\bin;%ProjectPath%\lib\*
java org.testng.TestNG %ProjectPath%\lib\TestNG.xml
popd
pause