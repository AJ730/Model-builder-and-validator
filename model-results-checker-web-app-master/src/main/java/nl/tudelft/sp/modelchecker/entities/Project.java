package nl.tudelft.sp.modelchecker.entities;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "project")
public class Project implements SuperEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private ProjectHolder projectHolder;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Container> containers = new HashSet<>();

    /**
     * Constructor for Project.
     *
     * @param title       the title of the project
     * @param description the description of the project
     */
    public Project(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Remove foreignKey Constraints.
     */
    @PreRemove
    public void remove() {
        Set<Project> projects = new LinkedHashSet<>(projectHolder.getProjects()) {
        };
        projects.remove(this);
        projectHolder.setProjects(projects);

        Set<Project> adminProjects = new LinkedHashSet<>(admin.getProjects()) {
        };
        adminProjects.remove(this);
        admin.setProjects(adminProjects);
    }

}
