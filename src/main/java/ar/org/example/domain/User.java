package ar.org.example.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A User.
 */
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    @Size(max = 32)
    @Column(name = "name", length = 32)
    private String name;

    @Size(max = 32)
    @Column(name = "surname", length = 32)
    private String surname;

    @NotNull
    @Size(max = 32)
    @Column(name = "login", length = 32, nullable = false)
    private String login;

    @NotNull
    @Size(max = 64)
    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Size(max = 128)
    @Column(name = "email", length = 128)
    private String email;

    @NotNull
    @Max(value = 1)
    @Column(name = "enabled", nullable = false)
    private Integer enabled;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public User name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public User surname(String surname) {
        this.surname = surname;
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public User login(String login) {
        this.login = login;
        return this;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public User enabled(Integer enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User roles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public User addRoles(Role role) {
        this.roles.add(role);
        return this;
    }

    public User removeRoles(Role role) {
        this.roles.remove(role);
        return this;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (user.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", surname='" + getSurname() + "'" +
                ", login='" + getLogin() + "'" +
                ", password='" + getPassword() + "'" +
                ", email='" + getEmail() + "'" +
                ", enabled=" + getEnabled() +
                "}";
    }
}
