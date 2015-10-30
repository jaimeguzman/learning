
  class Trie[V](key: Option[Char]) {

    def this() {
      this(None);
    }

    import scala.collection.Seq
    import scala.collection.immutable.TreeMap
    import scala.collection.immutable.WrappedString
    import scala.math.LowPriorityOrderingImplicits


    private var nodes = new TreeMap[Char, Trie[V]]; 

    var value : Option[V] = None;

    def put(k: String, v: V) : Trie[V] = {

      normalise(k) match {
        case Seq()         => this.value = Some(v); this;
        case Seq(h,t @ _*) => {
          val node = this.nodes.get(h) match {
            case None => {
              val n = new Trie[V](Some(h));
              this.nodes = this.nodes.insert(h, n);
              n;
            }
            case Some(n) => n
          }
          node.put(t,v);
        }
      }
    }


    private def normalise(s: String) : WrappedString = {
      new WrappedString(s.toLowerCase.replaceAll("""\W+""", ""))
    }

    def get(k: String): Option[V] = {
      nodeFor(k).flatMap { (n) => n.value }
    }

    def getAllWithPrefix(k: String) : Seq[V] = {
      nodeFor(k).toList.flatMap { (n) => n.getAll } 
    }

    def getAll : Seq[V] = {
      this.value.toList ++ this.nodes.values.flatMap { n => n.getAll }
    }

    override def toString = {
      key.toString ++ ":" ++ value.toString ++ "\n" ++ nodes.values.map { n => n.toString.split("\n").map { l => "  " + l }.mkString("\n") }.mkString("\n")
    }

    def nodeFor(k: String): Option[Trie[V]] =  {
      normalise(k) match {
        case Seq()          => Some(this)
        case Seq(h, t @ _*) => nodes.get(h).flatMap { n => n.nodeFor(t) }
      }
    }

    implicit def charSeq2WrappedString(s : Seq[Char]) : WrappedString =  {
      new WrappedString(s.mkString)
    }

    implicit def charSeq2String(s: Seq[Char]) : String = {
      s.mkString
    }

    implicit def catOptions[A](xs : Seq[Option[A]]) : Seq[A] = {
      xs.flatMap { (x) => x.toList }
    }
  }


  object trieTicom {
    System.out.println(" trieTicom is Running ");

    def main(args: Array[String]): Unit = {
      
      val alphabet  = List( 'A','B','C','R');  
      val alpha: Option[Char] = Some('A')
      val arbol     = new Trie() 

      val lista: Nothing = List('F, 'B, 'A, 'D, 'C, 'E, 'G, 'I, 'H)

      arbol.put("asd",lista)

      System.out.println(  arbol.toString )
      System.out.println(  arbol.getAll   )

    }


  }
  



