package it.workstocks.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity<E extends Serializable> {

	public abstract E getId();

	public abstract void setId(E id);

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	private void creationTime() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	private void updateTime() {
		this.updatedAt = LocalDateTime.now();
	}

}
