package edu.cmu.lti.f13.hw4.hw4_wfeely.casconsumers;

import java.io.IOException;
import java.util.*;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_wfeely.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /** query id number **/
  public ArrayList<Integer> qIdList;

  /** query and text relevant values **/
  public ArrayList<Integer> relList;

  /** global vocabulary **/
  public HashMap<String, Integer> vocab;
  
  /** document vocabulary **/
  public HashMap<String, HashMap<String, Integer>> docVocab;

  public void initialize() throws ResourceInitializationException {

    qIdList = new ArrayList<Integer>();

    relList = new ArrayList<Integer>();

    vocab = new HashMap<String, Integer>();
    
    docVocab = new HashMap<String, HashMap<String, Integer>>();
  }

  /**
   * DONE: 1. construct the global word dictionary 2. keep the word frequency for each sentence
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();

    if (it.hasNext()) {
      // set up document
      Document doc = (Document) it.next();
      String docText = doc.getText();
      
      // set up tokenList for this document
      FSList fsTokenList = doc.getTokenList();
      ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
      
      // set up vocabulary for this document
      docVocab.put(docText, new HashMap<String, Integer>());
      
      // add QueryID and RelevanceValue to qId list and rel list
      qIdList.add(doc.getQueryID());
      relList.add(doc.getRelevanceValue());

      // DEBUG: Print document
      System.out.println(doc.getText());
      
      // update global vocabulary with tokens from this document
      for (Token tok : tokenList) {
        String word = tok.getText();
        Integer freq = tok.getFrequency();
        // put each token into global vocabulary hash map, updating global frequencies
        if (vocab.containsKey(word))
          vocab.put(word, freq+1);
        else
          vocab.put(word, freq);
        // put each token into document vocabulary hash map
        docVocab.get(docText).put(word, freq);
      }
      // DEBUG: print all elements in vocab
      if (vocab != null) {
        for (String word : vocab.keySet()) {
          System.out.println(word + "," + vocab.get(word));
        }
      }

    }

  }

  /**
   * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2. Compute the MRR metric
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    // TODO :: compute the cosine similarity measure

    // TODO :: compute the rank of retrieved sentences

    // TODO :: compute the metric:: mean reciprocal rank
    double metric_mrr = compute_mrr();
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
  }

  /**
   * 
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;

    // TODO :: compute cosine similarity between two sentences

    return cosine_similarity;
  }

  /**
   * 
   * @return mrr
   */
  private double compute_mrr() {
    double metric_mrr = 0.0;

    // TODO :: compute Mean Reciprocal Rank (MRR) of the text collection

    return metric_mrr;
  }

}
