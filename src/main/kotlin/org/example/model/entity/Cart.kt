package org.example.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.example.org.example.model.User

@Entity
@Table(name = "carts")
class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JsonBackReference("user-cart")
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User? = null,

    @JsonManagedReference("cart-items")
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var items: MutableList<CartItem> = mutableListOf()
)
