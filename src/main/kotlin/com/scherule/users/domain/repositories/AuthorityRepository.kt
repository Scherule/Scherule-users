package com.scherule.users.domain.repositories

import com.scherule.users.domain.models.Authority
import com.scherule.users.domain.models.AuthorityName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, AuthorityName>