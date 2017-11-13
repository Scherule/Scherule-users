package com.scherule.users.repositories

import com.scherule.users.models.Authority
import com.scherule.users.models.AuthorityName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, AuthorityName>