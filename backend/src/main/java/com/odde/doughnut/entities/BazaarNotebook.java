package com.odde.doughnut.entities;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bazaar_notebook")
public class BazaarNotebook {
  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "notebook_id", referencedColumnName = "id")
  @Getter
  @Setter
  private Notebook notebook;
}
