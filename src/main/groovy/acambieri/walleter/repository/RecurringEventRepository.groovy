package acambieri.walleter.repository


import acambieri.walleter.model.ScheduledEvent
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface RecurringEventRepository extends CrudRepository<ScheduledEvent,Long> {
    @Query("""select r from ScheduledEvent r
                        where r.enabled = true 
                        and r.nextFire <= current_date()
                        order by r.nextFire""")
    List<ScheduledEvent> listEventsToFire();
}