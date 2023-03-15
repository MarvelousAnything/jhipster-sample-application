package com.marvelousanything.jhipster.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recipe")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "prep_time")
    private Integer prepTime;

    @Column(name = "cook_time")
    private Integer cookTime;

    @Column(name = "servings")
    private Integer servings;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "recipe")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "ingredient", "unit", "recipe" }, allowSetters = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "recipe" }, allowSetters = true)
    private Set<Instruction> instructions = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "recipes" }, allowSetters = true)
    private Author author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Recipe name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Recipe description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrepTime() {
        return this.prepTime;
    }

    public Recipe prepTime(Integer prepTime) {
        this.setPrepTime(prepTime);
        return this;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public Integer getCookTime() {
        return this.cookTime;
    }

    public Recipe cookTime(Integer cookTime) {
        this.setCookTime(cookTime);
        return this;
    }

    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }

    public Integer getServings() {
        return this.servings;
    }

    public Recipe servings(Integer servings) {
        this.setServings(servings);
        return this;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Recipe imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<RecipeIngredient> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(Set<RecipeIngredient> recipeIngredients) {
        if (this.ingredients != null) {
            this.ingredients.forEach(i -> i.setRecipe(null));
        }
        if (recipeIngredients != null) {
            recipeIngredients.forEach(i -> i.setRecipe(this));
        }
        this.ingredients = recipeIngredients;
    }

    public Recipe ingredients(Set<RecipeIngredient> recipeIngredients) {
        this.setIngredients(recipeIngredients);
        return this;
    }

    public Recipe addIngredients(RecipeIngredient recipeIngredient) {
        this.ingredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
        return this;
    }

    public Recipe removeIngredients(RecipeIngredient recipeIngredient) {
        this.ingredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
        return this;
    }

    public Set<Instruction> getInstructions() {
        return this.instructions;
    }

    public void setInstructions(Set<Instruction> instructions) {
        if (this.instructions != null) {
            this.instructions.forEach(i -> i.setRecipe(null));
        }
        if (instructions != null) {
            instructions.forEach(i -> i.setRecipe(this));
        }
        this.instructions = instructions;
    }

    public Recipe instructions(Set<Instruction> instructions) {
        this.setInstructions(instructions);
        return this;
    }

    public Recipe addInstructions(Instruction instruction) {
        this.instructions.add(instruction);
        instruction.setRecipe(this);
        return this;
    }

    public Recipe removeInstructions(Instruction instruction) {
        this.instructions.remove(instruction);
        instruction.setRecipe(null);
        return this;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Recipe author(Author author) {
        this.setAuthor(author);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }
        return id != null && id.equals(((Recipe) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", prepTime=" + getPrepTime() +
            ", cookTime=" + getCookTime() +
            ", servings=" + getServings() +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
