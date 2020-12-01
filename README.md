# SentenceSimplifier
The SentenceSimplifier is rule-based algorithm for simplifying linguistically complex sentences. An extra feature for selecting-candidate sentences is implemented, which is based on POS-Tag matching. A sentence is a candidate, when it contains at least one entity of the type person, location or organization.
This sentence simplifier is used to evaluate knowledge extraction frameworks, like Stanford and FOX.

This is a BachelorThesis project. It consists  reimplementation of the approach from Heilman and Smith (http://www.cs.cmu.edu/~./mheilman/papers/heilman-smith-qg-extr-facts.pdf) and applying some modifications, as extracting ADJPs, spliting 'not-only-but-also' structures and improved punctuation of short-names (U. K. --> U.K.).

![flow-of-simplif](https://user-images.githubusercontent.com/44163172/100740195-98890080-33d8-11eb-94fb-8eb9d30b1847.png)


# Requirements
Java 13, Maven 4

# Main simplification methods:
1. App.simplifyDocument(); //simplifies the text, mainly by splitting sentences. HS-extended in Bachelorthesis := HS-original + 2-3 new features.

2. SentenceSimplifier.main(); // main SS class from HS-original algorithm

3. POSMarker.checkRelevance(); //true, if entities present in a text. 

4. App.simplifyDocumentIfEntityPresent(); // 2. + 1.
