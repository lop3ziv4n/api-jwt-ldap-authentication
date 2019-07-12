package ar.org.example.service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the User entity.
 */
public class UserDTO implements Serializable {

    private Long id;

    @Size(max = 32)
    private String name;

    @Size(max = 32)
    private String surname;

    @NotNull
    @Size(max = 32)
    private String login;

    @NotNull
    @Size(max = 64)
    private String password;

    @Size(max = 128)
    private String email;

    @NotNull
    @Max(value = 1)
    private Integer enabled;

    private Set<RoleDTO> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
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

        UserDTO userDTO = (UserDTO) o;
        if (userDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserDTO{" +
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
