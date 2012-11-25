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
    val tagger = new MaxentTagger("english.tagger")
    if(true) {
      (1 to 1000).foreach { i =>
        timeIt("tag") {
          (1 to 100).foreach { j =>
            tagger.tagString(txt)
          }
          //println("    getTag = " + tagger.took/1000);
          //tagger.took = 0L;
        }
      }
    } else {
      println(tagger.tagString(txt));
    }
  }


  def txt = """
Note to Chevrolet: There are some things about the ’80s that aren’t worthy of nostalgia. The era’s pony cars, by and large, fall under this umbrella. It’s true that Ford has the five-point-oh—a designation fairly soaked in Aqua Net, yet perhaps the only thing about the decade’s Stangs worth revisiting—but when it named its latest track-oriented model, it resurrected “Boss.” First appearing in 1969, Boss is cool. It’s a little intimidating, and it’s also Bruce Springsteen’s nickname. It sounds bad-ass. Chevrolet’s 1LE? For the 17 people who were paying attention in 1988, it conjures memories of an option bundle that only made that year’s Camaro less bad. To the rest of us, it sounds like three randomly selected characters.

Although it still seems as though the designation had been plucked from an alphanumeric bingo bucket, 1LE has new significance for 2013 as an option package for manual-transmission Camaro SS models. It brings a host of upgrades ported from or inspired by the mighty ZL1. The pieces that make the most difference fall into the latter category, however, including the front anti-roll-bar mounting setup, 10-by-20-inch front wheels and 11-by-20-inch rears, and beefier half-shafts. The equipment pilfered directly from the ZL1 includes wheel bearings, toe links, rear shock mounts, the fuel pump, the flat-bottom steering wheel, the short-throw six-speed manual and its transmission cooler, and 285/35-20 Goodyear Eagle F1 Supercar G: 2 tires all around (the ZL1 gets 305/35-20 rubber at the rear). The beefy wheel-and-tire combo actually saves a total of 22 pounds of unsprung weight.


The 1LE package also comes with a shorter final-drive ratio of 3.91:1 versus the 3.45:1 rear end in the SS and adds monotube rear dampers—instead of the SS’s twin tubes—and a strut-tower brace. As for externals, Chevy engineers tell us that even though the 1LE’s splitter increases downforce in the front, the wider wheels generate enough lift to bring the 1LE right back in line with the SS. Out back, the SS’s lip spoiler carries over to 1LE cars.

 
Character Study

The 1LE exceeds its status as an option package in that it truly changes the character of the SS—and we’re not talking about the matte-black hood. (It’s a wrap, and it looks unfinished.) The difference becomes apparent when you throw the Camaro 1LE into a corner. Where the SS understeers, the 1LE darts toward apexes. The Camaro still feels like a behemoth—that problem is deeply rooted in its squinty greenhouse and a curb weight we estimate at 3900 pounds—but the newfound eagerness scrubs away its greatest dynamic flaw. The electronic power-steering system (standard on all SS models and shared with the ZL1) is a fine representative of the breed, with a progressive weighting and natural feel. The 1LE doesn’t represent as comprehensive a revision as does its direct competitor, the Boss 302, but neither is it quite as expensive.

For 2013, both the SS and 1LE equipped with manual transmissions offer a dual-mode exhaust system like that on the ZL1, where baffles open under increased load. The car we drove had it, and we wouldn’t recommend any Camaro owner try to live without it. For such a dramatically shaped vehicle, the SS sounds kind of wimpy. This exhaust gives the car more of the tympanic-membrane-shredding roar that makes the Corvette such an aural treat.

The 1LE’s sub-$40,000 starting point represents a premium of about 10 percent over the SS’s base sticker. For buyers whose Camaros will ever see track duty—or a 10Best-winning Boss 302 on a twisty road—it’s an investment in your own happiness. The 1LE name might not hark to the muscle-car heyday, but the car on which it’s affixed is good enough to make us remember an ’80s footnote a little more fondly.
  """
}
