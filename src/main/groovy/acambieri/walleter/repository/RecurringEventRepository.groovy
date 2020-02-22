package acambieri.walleter.repository

import acambieri.walleter.model.RecurringEvent
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.CrudRepositoryExtensionsKt

interface RecurringEventRepository extends CrudRepository<RecurringEvent,Long> {
    @Query("""select r from RecurringEvent r
                        where r.enabled = true 
                        and r.nextFire < current_date
                        order by r.nextFire""")
    List<RecurringEvent> listEventsToFire();
}