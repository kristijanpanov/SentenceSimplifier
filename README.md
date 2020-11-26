# SentenceSimplifier
The SentenceSimplifier is rule-based algorithm for simplifying linguistically complex sentences. An extra feature for selecting-candidate sentences is implemented, which is based on POS-Tag matching. A sentence is a candidate, when it contains at least one entity of the type person, location or organization.
This sentence simplifier is used to evaluate knowledge extraction frameworks, like Stanford and FOX.

This is a BachelorThesis project. It consists  reimplementation of the approach from Heilman and Smith (http://www.cs.cmu.edu/~./mheilman/papers/heilman-smith-qg-extr-facts.pdf) and applying some modifications, as extracting ADJPs and spliting 'not-only-but-also' structures.

# Requirements
Java 13, Maven 4

# Main simplification methods:
App.simplifyDocument(); //simplifies the text, mainly by splitting sentences. HS-extended in Bachelorthesis := HS-approach + 2-3 new rules.

App.simplifyDocumentIfEntityPresent(); //have to be tested
