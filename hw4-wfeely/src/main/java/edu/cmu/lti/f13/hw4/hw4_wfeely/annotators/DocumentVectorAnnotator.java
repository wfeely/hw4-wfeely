/** DocumentVectorAnnotator.java
 * @author Weston Feely
 */
package edu.cmu.lti.f13.hw4.hw4_wfeely.annotators;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_wfeely.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
    }

  }

  /**
   * 
   * @param jcas
   * @param doc
   */
  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();
    // construct a vector of tokens and update the tokenList in CAS
    ArrayList<Token> aList = new ArrayList<Token>();
    // search for tokens in each document
    Pattern tokenPattern = Pattern.compile("[A-Za-z0-9']+");
    int pos = 0;
    Matcher matcher = tokenPattern.matcher(docText);
    while (matcher.find(pos)) {
      // found a token; create annotation
      Token token = new Token(jcas);
      token.setBegin(matcher.start());
      token.setEnd(matcher.end());
      token.setText(docText.substring(token.getBegin(), token.getEnd()).toLowerCase());
      token.setFrequency(1);
      // add token to indexes and iterate
      token.addToIndexes();
      pos = matcher.end();
      // loop through tokens in aList
      boolean match = false;
      for (Token aListTok : aList) {
        // compare document token with current token from arrayList aList
        if (token.getText() == aListTok.getText()) {
          // match; increment frequency of this type and set match to true, and break
          aListTok.setFrequency(aListTok.getFrequency() + 1);
          match = true;
          break;
        }
      }
      // no match; add this new type to aList
      if (!match)
        aList.add(token);
    }
    // convert array list of tokens into FSList, set tokenList as this new FSList
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, aList));
    // DEBUG: print all elements in tokenList
    /*
     * int i = 0; Token tok = null; while (true) { try { tok = (Token)
     * doc.getTokenList().getNthElement(i); } catch (Exception e) { // no more elements to process
     * break; } System.out.println("FSList contains: " + tok.getText() + "," + tok.getFrequency());
     * i++; }
     */
  }
}