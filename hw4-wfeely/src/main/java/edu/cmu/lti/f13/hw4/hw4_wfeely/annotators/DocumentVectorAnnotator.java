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

  /** list of stopwords **/
  public ArrayList<String> stoplist;

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    // set up stoplist
    stoplist = new ArrayList<String>();
    String stopwords = "a about above after again against all am an and any are aren't as at be because been before being below between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves";

    for (String stopword : stopwords.split(" ")) {
      stoplist.add(stopword);
    }

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
      int end = token.getEnd() - 1;
      token.setText(docText.substring(token.getBegin(), end).toLowerCase());
      token.setFrequency(1);
      // check token against stoplist
      if (stoplist.contains(token.getText())) {
        pos = matcher.end();
        continue;
      }
      // add token to indexes and iterate
      token.addToIndexes();
      pos = matcher.end();
      // loop through tokens in aList
      boolean match = false;
      for (Token aListTok : aList) {
        // compare document token with current token from arrayList aList
        if (token.getText().equals(aListTok.getText())) {
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
     * System.out.println("Doc: " + doc.getText()); for (Token t :
     * Utils.fromFSListToCollection(doc.getTokenList(), Token.class))
     * System.out.println("TokenList contains: " + t.getText() + "," + t.getFrequency());
     */
  }
}