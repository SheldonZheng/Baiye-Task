/**
  * Created by Baiye on 2016/9/23.
  */
object EchoClientScalaMain {

  def main(args: Array[String]) {
    var client = new EchoClientScala(9999,"127.0.0.1")
    client.run()
  }

}