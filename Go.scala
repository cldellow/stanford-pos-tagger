import java.io._;
import java.util._;
import java.io.StringReader;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

object MyScalaApp {
  def timeIt[A](msg: String)(f: => A): A = {
   val st = System.nanoTime
   val rv = f
   println(" " + msg + ": " + (System.nanoTime - st)/1000)
   rv
  }

  def main(args: Array[String]) {
    System.out.println("waiting");
    val txt = scala.io.Source.fromFile("./betel.txt").getLines().mkString(" ")
    val tagger = new MaxentTagger("english.tagger")
    if(false) {
      (1 to 1000).foreach { i =>
        timeIt("tag") {
          (1 to 10).foreach { j =>
            tagger.tagString(txt)
          }
//          println("    getTag = " + tagger.took/1000);
//          tagger.took = 0;
        }
      }
    } else {
      println(tagger.tagString(txt));
    }
  }
}
