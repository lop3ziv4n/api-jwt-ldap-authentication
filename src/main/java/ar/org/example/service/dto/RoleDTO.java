package ar.org.example.service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Role entity.
 */
public class RoleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 32)
    private String code;

    @Size(max = 128)
    private String description;

    @NotNull
    @Max(value = 1)
    private Integer enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleDTO roleDTO = (RoleDTO) o;
        if (roleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), roleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + getId() +
                ", code='" + getCode() + "'" +
                ", description='" + getDescription() + "'" +
                ", enabled=" + getEnabled() +
                "}";
    }
}
