package com.bloemist.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceIdGenerator")
  @GenericGenerator(name = "sequenceIdGenerator", strategy = "sequence", parameters = {
      @Parameter(name = SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, value = SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX),
      @Parameter(name = SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true"),
      @Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "1"),
      @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1")})
  @Column(name = "id", updatable = false)
  private Long id;
}
