package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@EqualsAndHashCode(includes='name')
@ToString(includes='name', includeNames=true, includePackage=false)
@Entity
class Role implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	String name;

}
