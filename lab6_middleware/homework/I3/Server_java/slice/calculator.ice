
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
    void op(A a1, short b1);

    PowSeqResults pow(PowSeq inputSeq);
    double avg(PowSeqResults input);
  };

};

#endif

//slice2java --output-dir generated slice/calculator.ice