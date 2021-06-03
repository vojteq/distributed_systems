
#ifndef CALC_ICE
#define CALC_ICE

module Demo
{
  enum operation { MIN, MAX, AVG };
  
  exception NoInput {};

  struct A
  {
    short a;
    long b;
    float c;
    string d;
  };

  sequence <int> Seq;

    struct PowSeq {
        int exponent;
        Seq seq;
    };

  sequence <PowSeq> PowSeqResult;

  struct PowSeqResults {
    PowSeqResult sequences;
  };

  interface Calc
  {
    long add(int a, int b);
    long subtract(int a, int b);
    void op(A a1, short b1); //za��my, �e to te� jest operacja arytmetyczna ;)

    PowSeqResults pow(PowSeq inputSeq);
  };

};

#endif

//slice2java --output-dir generated slice/calculator.ice