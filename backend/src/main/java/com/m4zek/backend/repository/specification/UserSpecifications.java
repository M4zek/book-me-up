package com.m4zek.backend.repository.specification;

import com.m4zek.backend.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpecifications {
    public static Specification<User> searchByTokens(List<String> tokens, List<UserStatus> statuses, List<RoleEnum> roles) {
        return (root, query, cb) -> {


            List<Predicate> predicates = new ArrayList<>();

            /*
             * TOKENS
             */
            if (tokens != null && !tokens.isEmpty()) {

                Join<User, UserData> userData =
                        root.join("userData");

                List<Predicate> tokenPredicates = new ArrayList<>();

                for (String token : tokens) {
                    String search =
                            "%" + token.toLowerCase() + "%";

                    Predicate firstName = cb.like(
                            cb.lower(userData.get("firstName")),
                            search
                    );

                    Predicate lastName = cb.like(
                            cb.lower(userData.get("lastName")),
                            search
                    );

                    Predicate email = cb.like(
                            cb.lower(root.get("addressEmail")),
                            search
                    );

                    tokenPredicates.add(
                            cb.or(
                                    firstName,
                                    lastName,
                                    email
                            )
                    );
                }

                predicates.add(cb.and(tokenPredicates.toArray(new Predicate[0])));
            }

            /*
             * STATUS
             */
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            /*
             * ROLE
             */
            if (roles != null && !roles.isEmpty()) {
                Join<User, Role> role = root.join("roles");

                predicates.add(role.get("name").in(roles.stream().map(String::valueOf).collect(Collectors.toSet())));

                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
