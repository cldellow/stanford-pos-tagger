package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.InDataStreamFile;
import edu.stanford.nlp.io.OutDataStreamFile;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.tagger.common.TaggerConstants;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.HashIndex;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

/**
 * This class holds the POS tags, assigns them unique ids, and knows which tags
 * are open versus closed class.
 * <p/>
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Company:      Stanford University<p>
 *
 * @author Kristina Toutanova
 * @version 1.0
 */
public class TTags {

  private Index<String> index = new HashIndex<String>();
  private final HashSet<String> closed = new HashSet<String>();
  private HashSet<String> openTags = null; /* cache */
  private final boolean isEnglish; // for speed
  private static final boolean doDeterministicTagExpansion = true;


  /** If true, then the open tags are fixed and we set closed tags based on
   *  index-openTags; otherwise, we set open tags based on index-closedTags.
   */
  private boolean openFixed = false;

  /** When making a decision based on the training data as to whether a
   *  tag is closed, this is the threshold for how many tokens can be in
   *  a closed class - purposely conservative.
   * TODO: make this an option you can set
   */
  private final int closedTagThreshold = Integer.valueOf(TaggerConfig.CLOSED_CLASS_THRESHOLD);

  /** If true, when a model is trained, all tags that had fewer tokens than
   *  closedTagThreshold will be considered closed.
   */
  private boolean learnClosedTags = false;


  public TTags() {
    isEnglish = false;
  }

  /*
  public TTags(TaggerConfig config) {
    String[] closedArray = config.getClosedClassTags();
    String[] openArray = config.getOpenClassTags();
    if(closedArray.length > 0) {
      closed = new HashSet<String>(Arrays.asList(closedArray));
    } else if(openArray.length > 0) {
      openTags = new HashSet<String>(Arrays.asList(openArray));
    } else {
      learnClosedTags = config.getLearnClosedClassTags();
      closedTagThreshold = config.getClosedTagThreshold();
    }
  }
  */

  TTags(String language) {
    if (language.equalsIgnoreCase("english")) {
      closed.add(".");
      closed.add(",");
      closed.add("``");
      closed.add("''");
      closed.add(":");
      closed.add("$");
      closed.add("EX");
      closed.add("(");
      closed.add(")");
      closed.add("#");
      closed.add("MD");
      closed.add("CC");
      closed.add("DT");
      closed.add("LS");
      closed.add("PDT");
      closed.add("POS");
      closed.add("PRP");
      closed.add("PRP$");
      closed.add("RP");
      closed.add("TO");
      closed.add(TaggerConstants.EOS_TAG);
      closed.add("UH");
      closed.add("WDT");
      closed.add("WP");
      closed.add("WP$");
      closed.add("WRB");
      closed.add("-LRB-");
      closed.add("-RRB-");
      //  closed.add("IN");
      isEnglish = true;
    } else if(language.equalsIgnoreCase("polish")) {
      closed.add(".");
      closed.add(",");
      closed.add("``");
      closed.add("''");
      closed.add(":");
      closed.add("$");
      closed.add("(");
      closed.add(")");
      closed.add("#");
      closed.add("POS");
      closed.add(TaggerConstants.EOS_TAG);
      closed.add("ppron12");
      closed.add("ppron3");
      closed.add("siebie");
      closed.add("qub");
      closed.add("conj");
      isEnglish = false;
    } else if(language.equalsIgnoreCase("chinese")) {
      /* chinese treebank 5 tags */
      closed.add("AS");
      closed.add("BA");
      closed.add("CC");
      closed.add("CS");
      closed.add("DEC");
      closed.add("DEG");
      closed.add("DER");
      closed.add("DEV");
      closed.add("DT");
      closed.add("ETC");
      closed.add("IJ");
      closed.add("LB");
      closed.add("LC");
      closed.add("P");
      closed.add("PN");
      closed.add("PU");
      closed.add("SB");
      closed.add("SP");
      closed.add("VC");
      closed.add("VE");
      isEnglish = false;
    } else if (language.equalsIgnoreCase("arabic")) {
      // kulick tag set
      // the following tags seem to be complete sets in the training
      // data (see the comments for "german" for more info)
      closed.add("PUNC");
      closed.add("CC");
      closed.add("CPRP$");
      closed.add(TaggerConstants.EOS_TAG);
      // maybe more should still be added ... cdm jun 2006
      isEnglish = false;
    } else if(language.equalsIgnoreCase("german")) {
      // The current version of the German tagger is built with the
      // negra-tigra data set.  We use the STTS tag set.  In
      // particular, we use the version with the changes described in
      // appendix A-2 of
      // http://www.uni-potsdam.de/u/germanistik/ls_dgs/tiger1-intro.pdf
      // eg the STTS tag set with PROAV instead of PAV
      // To find the closed tags, we use lists of standard closed German
      // tags, eg
      // http://www.sfs.uni-tuebingen.de/Elwis/stts/Wortlisten/WortFormen.html
      // In other words:
      //
      // APPO APPR APPRART APZR ART KOKOM KON KOUI KOUS PDAT PDS PIAT
      // PIDAT PIS PPER PPOSAT PPOSS PRELAT PRELS PRF PROAV PTKA
      // PTKANT PTKNEG PTKVZ PTKZU PWAT PWAV PWS VAFIN VAIMP VAINF
      // VAPP VMFIN VMINF VMPP
      //
      // One issue with this is that our training data does not have
      // the complete collection of many of these closed tags.  For
      // example, words with the tag APPR show up in the test or dev
      // sets without ever showing up in the training.  Tags that
      // don't have this property:
      //
      // KOKOM PPOSS PTKA PTKNEG PWAT VAINF VAPP VMINF VMPP
      closed.add("$,");
      closed.add("$.");
      closed.add("$(");
      closed.add("--");
      closed.add(TaggerConstants.EOS_TAG);
      closed.add("KOKOM");
      closed.add("PPOSS");
      closed.add("PTKA");
      closed.add("PTKNEG");
      closed.add("PWAT");
      closed.add("VAINF");
      closed.add("VAPP");
      closed.add("VMINF");
      closed.add("VMPP");
      isEnglish = false;
    } else if (language.equalsIgnoreCase("french")) {
      // Using the french treebank, with Spence's adaptations of
      // Candito's treebank modifications, we get that only the
      // punctuation tags are reliably closed:
      // !, ", *, ,, -, -LRB-, -RRB-, ., ..., /, :, ;, =, ?, [, ]
      closed.add("!");
      closed.add("\"");
      closed.add("*");
      closed.add(",");
      closed.add("-");
      closed.add("-LRB-");
      closed.add("-RRB-");
      closed.add(".");
      closed.add("...");
      closed.add("/");
      closed.add(":");
      closed.add(";");
      closed.add("=");
      closed.add("?");
      closed.add("[");
      closed.add("]");
      isEnglish = false;
    } else if (language.equalsIgnoreCase("medpost")) {
      closed.add(".");
      closed.add(",");
      closed.add("``");
      closed.add("''");
      closed.add(":");
      closed.add("$");
      closed.add("EX");
      closed.add("(");
      closed.add(")");
      closed.add("VM");
      closed.add("CC");
      closed.add("DD");
      closed.add("DB");
      closed.add("GE");
      closed.add("PND");
      closed.add("PNG");
      closed.add("TO");
      closed.add(TaggerConstants.EOS_TAG);
      closed.add("-LRB-");
      closed.add("-RRB-");
      isEnglish = false;
    } else if (language.equalsIgnoreCase("")) {
      isEnglish = false;
    }
    /* add closed-class lists for other languages here */
    else {
      throw new RuntimeException("unknown language: " + language);
    }
  }


  /**
   * Returns a list of all open class tags
   * @return set of open tags
   */
  public Set<String> getOpenTags() {
    if (openTags == null) { /* cache check */
      HashSet<String> open = new HashSet<String>();

      for (String tag : index) {
        if ( ! closed.contains(tag)) {
          open.add(tag);
        }
      }

      openTags = open;
    } // if
    return openTags;
  }

  protected int add(String tag) {
    index.add(tag);
    return index.indexOf(tag);
  }

  public String getTag(int i) {
    return index.get(i);
  }

  protected void save(String filename,
                      HashMap<String, HashSet<String>> tagTokens) {
    try {
      DataOutputStream out = new OutDataStreamFile(filename);
      save(out, tagTokens);
      out.close();
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }

  protected void save(DataOutputStream file,
                      HashMap<String, HashSet<String>> tagTokens) {
    try {
      file.writeInt(index.size());
      for (String item : index) {
        file.writeUTF(item);
        if (learnClosedTags) {
          if (tagTokens.get(item).size() < closedTagThreshold) {
            markClosed(item);
          }
        }
        file.writeBoolean(isClosed(item));
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
  }


  protected void read(String filename) {
    try {
      InDataStreamFile in = new InDataStreamFile(filename);
      read(in);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void read(DataInputStream file) {
    try {
      int size = file.readInt();
      index = new HashIndex<String>();
      for (int i = 0; i < size; i++) {
        String tag = file.readUTF();
        boolean inClosed = file.readBoolean();
        index.add(tag);

        if (inClosed) closed.add(tag);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  protected boolean isClosed(String tag) {
    if (openFixed) {
      return !openTags.contains(tag);
    } else {
      return closed.contains(tag);
    }
  }

  void markClosed(String tag) {
    add(tag);
    closed.add(tag);
  }

  public void setLearnClosedTags(boolean learn) {
    learnClosedTags = learn;
  }

  public void setOpenClassTags(String[] openClassTags) {
    openTags = new HashSet<String>();
    openTags.addAll(Arrays.asList(openClassTags));
    for (String tag : openClassTags) {
      add(tag);
    }
    openFixed = true;
  }

  public void setClosedClassTags(String[] closedClassTags) {
    for(String tag : closedClassTags) {
      markClosed(tag);
    }
  }


  int getIndex(String tag) {
    return index.indexOf(tag);
  }

  public int getSize() {
    return index.size();
  }

  /**
   * Deterministically adds other possible tags for words given observed tags.
   * For instance, for English with the Penn POS tag, a word with the VB
   * tag would also be expected to have the VBP tag.
   * <p>
   * The current implementation is a bit contorted, as it works to avoid
   * object allocations wherever possible for maximum runtime speed. But
   * intuitively it's just: For English (only),
   * if the VBD tag is present but not VBN, add it, and vice versa;
   * if the VB tag is present but not VBN, add it, and vice versa.
   *
   * @param tags Known possible tags for the word
   * @return A superset of tags
   */
  String[] deterministicallyExpandTags(String[] tags) {
    if (isEnglish && doDeterministicTagExpansion) {
      boolean seenVBN = false;
      boolean seenVBD =	false;
      boolean seenVB =	false;
      boolean seenVBP = false;
      for (String tag : tags) {
        char ch = tag.charAt(0);
        if (ch == 'V') {
          if ("VBD".equals(tag)) {
            seenVBD = true;
          } else if ("VBN".equals(tag)) {
            seenVBN = true;
          } else if ("VB".equals(tag)) {
            seenVB = true;
          } else if ("VBP".equals(tag)) {
            seenVBP = true;
          }
        }
      }
      int toAdd = 0;
      if ((seenVBN ^ seenVBD)) { // ^ is xor
        toAdd++;
      }
      if (seenVB ^ seenVBP) {
        toAdd++;
      }
      if (toAdd > 0) {
        int ind = tags.length;
        String[] newTags = new String[ind + toAdd];
        System.arraycopy(tags, 0, newTags, 0, tags.length);
        if (seenVBN && ! seenVBD) {
          newTags[ind++] = "VBD";
        } else if (seenVBD && ! seenVBN) {
          newTags[ind++] = "VBN";
        }
        if (seenVB && ! seenVBP) {
          newTags[ind] = "VBP";
        } else if (seenVBP && ! seenVB) {
          newTags[ind] = "VB";
        }
        return newTags;
      } else {
        return tags;
      }
    } else {
      // no tag expansion for other languages currently
      return tags;
    }
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(index.toString());
    s.append(' ');
    if (openFixed) {
      s.append(" OPEN:").append(getOpenTags());
    } else {
      s.append(" open:").append(getOpenTags()).append(" CLOSED:").append(closed);
    }
    return s.toString();
  }
}
