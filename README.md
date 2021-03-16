# Joctave
run octave script in java

## usage

example:
```
  Joctave octave = new Joctave("usr/bin/octave");//create a session to octave
  String answer = octave.exec("a=1");//exec script
  System.out,println(answer);
  answer = octave.exec("a+1");//exec script
  System.out,println(answer);
  octave.close()//the session will automatic close when the program finish,or use this method to stop in advance
```

I have't test on windows,so you can use the following constructor to create init():


`Joctave octave = new Joctave(Process process)`

Suggest runing the following script first to run file .m:


`octave.exec("addpath(DIRPATH)")`
