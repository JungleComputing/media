@echo off

if "%OS%"=="Windows_NT" @setlocal

set ARGS=

:setupArgs
if ""%1""=="""" goto doneStart
set ARGS=%ARGS% %1
shift
goto setupArgs

:doneStart

java -cp ".;jars\*;external\*" -Djava.library.path=. ibis.media.test.ViewerDemo %ARGS%

