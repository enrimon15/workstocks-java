package it.workstocks.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseMongoEntity<E extends Serializable> implements Persistable<E> {
	
	public abstract E getId();

	public abstract void setId(E id);
	
	@CreatedDate
	private LocalDateTime createdAt;
	
	// per scatenare il @CreatedDate
	// evita l'update della createdAt se l'oggetto è già persistent
	@Override
    public boolean isNew() {
        return this.getId() == null;
    }

}
