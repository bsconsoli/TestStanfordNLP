import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;



class SFNLPFrenchParser {



    public static void main(String[] args) {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");
        SFNLPFrenchParser pd = new SFNLPFrenchParser();

        String textFile = (args.length > 0) ? args[0] : "Je suis Didier.";
        pd.parser(lp, textFile);
    }

    private void parser(LexicalizedParser lp, String filename) {

        ArrayList<Token> tokens = new ArrayList<>();
        int tokenNumber = 0;
        int sentenceNumber = 1;
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parse = lp.apply(sentence);
            ArrayList<Tree> nps = npExtractor(parse);
            System.out.println("Sentence " + sentenceNumber + ":");
            for(int i = 0; i < nps.size(); i++){
                System.out.println(nps.get(i).toString());
            }
            ArrayList<TaggedWord> words = parse.taggedYield();
            for(TaggedWord t: words) {
                tokens.add(tokenize(tokenNumber, sentenceNumber, t));
                tokenNumber++;
            }

            sentenceNumber++;

        }
    }

    private ArrayList<Tree> npExtractor(Tree parsedTree){
        ArrayList<Tree> nps = new ArrayList();
        Tree[] sent = parsedTree.children();
        for(int i = 0; i < sent.length; i++){
            if (sent[i].value().equalsIgnoreCase("NP")){
                nps.add(sent[i]);
                nps.addAll(npExtractor(sent[i]));
            } else{
                nps.addAll(npExtractor(sent[i]));
            }
        }
        return nps;
    }

    private static Token tokenize(int num, int sentNum, TaggedWord t){
        String word = t.toString();
        int wordBoundary = word.indexOf('/');
        return new Token(num, sentNum,word.substring(0,wordBoundary), word.substring(wordBoundary));
    }

    private SFNLPFrenchParser() {} // static methods only

}