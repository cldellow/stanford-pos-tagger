/**
 * Title:        StanfordMaxEnt<p>
 * Description:  A Maximum Entropy Toolkit<p>
 * Copyright:    Copyright (c) Kristina Toutanova<p>
 * Company:      Stanford University<p>
 */


package edu.stanford.nlp.tagger.maxent;

import java.io.DataInputStream;
import java.io.IOException;

import edu.stanford.nlp.io.OutDataStreamFile;


/**
 * Stores a triple of an extractor ID, a feature value (derived from history)
 * and a y (tag) value.  Used to compute a feature number in the loglinear
 * model.
 *
 * @author Kristina Toutanova, with minor changes by Daniel Cer
 * @version 1.0
 */
public class FeatureKey {

  // this object is used as a hash key and such instances should be treated as read-only
  // TODO: refactor code so that FeatureKeys are immutable? Or is the object reuse in a tight loop worth it?
  int num;
  String val;
  String tag;

  public FeatureKey() {
  }


  protected FeatureKey(int num, String val, String tag) {
    this.num = num;
    this.val = val;
    this.tag = tag;
  }


  protected void set(int num, String val, String tag) {
    this.num = num;
    this.val = val;
    this.tag = tag;
    this.hashCode = 0;
  }

  // If you know you're setting just the tag, this goes a bit faster
  // than doing all of the other fiddly stuff with the hash code later
  // on.  For some reason, though, passing in cached hashCodes doesn't
  // help.  Perhaps Strings already cache their own hashCode.
  protected void setTag(String tag) {
    if (hashCode != 0) {
      this.hashCode = 0;
      //this.hashCode = this.hashCode ^ this.tag.hashCode() ^ tag.hashCode();
    }
    this.tag = tag;
  }


  @Override
  public String toString() {
    return Integer.toString(num) + ' ' + val + ' ' + tag;
  }

  protected void save(OutDataStreamFile f) throws IOException {
    f.writeInt(num);
    f.writeUTF(val);
    f.writeUTF(tag);
  }

  protected void read(DataInputStream inf) throws IOException {
    num = inf.readInt();
    // mg2008: slight speedup:
    val = inf.readUTF();
    // intern the tag strings as they are read, since there are few of them. This saves tons of memory.
    tag = inf.readUTF();
    hashCode = 0;
  }

 /* --------------------
  * this was to clean-up some empties left from before
  *
  String cleanup(String val) {

    int index = val.indexOf('!');
    if (index > -1) {
      String first = val.substring(0, index);
      String last = val.substring(index + 1);
      System.out.println("in " + first + " " + last);
      first = TestSentence.toNice(first);
      last = TestSentence.toNice(last);
      System.out.println("out " + first + " " + last);
      return first + '!' + last;
    } else {
      return val;
    }
  }

  ---------- */

  private int hashCode = 0;
  @Override
  public int hashCode() {
    /* I'm not sure why this is happening, and i really don't want to
       spend a month tracing it down. -wmorgan. */
    //if (val == null) return num << 16 ^ 1 << 5 ^ tag.hashCode();
    //return num << 16 ^ val.hashCode() << 5 ^ tag.hashCode();
    if (hashCode == 0) {
      int hNum = Integer.rotateLeft(num,16);
      int hVal = Integer.rotateLeft(val.hashCode(),5);
      hashCode =  hNum ^ hVal ^ tag.hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    assert(o instanceof FeatureKey);
    FeatureKey f1 = (FeatureKey) o;
    return (num == f1.num) && (tag.equals(f1.tag)) && (val.equals(f1.val));
  }

}
