package libs.mongo

import com.novus.salat.dao.SalatDAO
import se.radley.plugin.salat._


class DB [ObjectType <: AnyRef : Manifest, ID <: Any : Manifest] {
  def withDao[A](block: SalatDAO[ObjectType,ID] => A): A = {
    val dao = new SalatDAO[ObjectType,ID](collection = mongoCollection("users")) {}
    block(dao)
  }
}
