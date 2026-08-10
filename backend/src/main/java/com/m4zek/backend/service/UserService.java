package com.m4zek.backend.service;

import com.m4zek.backend.exception.UserNotFoundException;
import com.m4zek.backend.model.Role;

import com.m4zek.backend.model.User;
import com.m4zek.backend.model.UserData;
import com.m4zek.backend.model.UserStatus;
import com.m4zek.backend.model.RoleEnum;
import com.m4zek.backend.model.dto.write.UserRequest;
import com.m4zek.backend.repository.UserRepository;
import com.m4zek.backend.repository.specification.UserSpecifications;
import com.m4zek.backend.security.service.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }


    public User createAndSaveUser(UserRequest request){
        Role userRole = this.roleService.findRoleUser()
                .orElseThrow(() -> new UserNotFoundException("User role not found"));

        Set<Role> roles = Set.of(userRole);

        UserData userData = new UserData(
                request.getUserData().getFirstName(),
                request.getUserData().getLastName(),
                request.getUserData().getDateOfBirth(),
                request.getUserData().getPhoneNumber()
        );

        User user = new User(
                request.getAddressEmail(),
                passwordEncoder.encode(request.getPassword()),
                userData,
                roles
        );

        return this.save(user);
    }

    public User save(User newUser){
        User user = this.userRepository.save(newUser);
        logger.info("User has been saved [{}]", user.getAddressEmail());
        return user;
    }

    public Optional<User> findById(int id){
        return this.userRepository.findById(id);
    }

    public User findUserById(int id){
        return this.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with given id not found"));
    }

    public List<User> findAllByIds(List<Integer> ids){
        return this.userRepository.findAllByIdIn(ids);
    }

    public boolean userExistsByEmail(String email){
        return this.userRepository.existsByAddressEmail(email);
    }

    public User findLoggedUser(){
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.findUserByEmail(myUserDetails.getEmail());
    }

    public User findUserByEmail(String email) {
        return this.userRepository.findByAddressEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email [" + email + "] not found")
        );
    }


    public Page<User> findUsersByNameAndSurname(Pageable pageable, String firstName, String lastName){
        return this.userRepository.findAllByFirstNameAndLastname(pageable, firstName, lastName);
    }


    public long getTotalUsersCount(){
        return this.userRepository.countAllUsers();
    }

    public double getUserGrowthPercentage(int days) {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime endOfPeriodA = today.atTime(LocalTime.MAX);

        LocalDateTime startOfPeriodA = today.minusDays(days).atStartOfDay();

        LocalDateTime startOfPeriodB = today.minusDays(2L * days).atStartOfDay();

        long periodA = userRepository.countUsersCreatedBetween(startOfPeriodA, endOfPeriodA);
        long periodB = userRepository.countUsersCreatedBetween(startOfPeriodB, startOfPeriodA);

        if (periodB == 0) {
            return periodA > 0 ? 100.0 : 0.0;
        }

        double growth = ((double) periodA - periodB) / periodB * 100.0;

        return Math.round(growth * 10.0) / 10.0;
    }

    // Return Map - Month -> Value (User registration)
    public Map<String, Long> getUserGrowthTrend(){

        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDateTime now = LocalDateTime.now(zone);
        LocalDateTime yearAgo = now.minusYears(1).minusDays(now.getDayOfMonth() - 1);

        List<Object[]> dbResult = this.userRepository.countGrowthBetweenDate(yearAgo, now);

        Map<String, Long> result = dbResult.stream()
                .collect(
                        Collectors.toMap(
                                row -> ((String) row[0]).toUpperCase(),
                                row -> (Long) row[1]
                        )
                );

        for(Month m: Month.values()){
            if(!result.containsKey(m.name())){
                result.put(m.name(), 0L);
            }
        }

        return result;
    }


    public Long countTodayCreatedUser(){
        ZoneId zone = ZoneId.of("Europe/Warsaw");

        LocalDateTime startOfDay = LocalDate.now(zone).atStartOfDay(); // Date ex: 2026-10-10T00:00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // Date ex: 2026-10-10T23:59:59.9999

        return this.userRepository.countUsersCreatedBetween(startOfDay, endOfDay);
    }


    public Page<User> findLastCreatedAccounts(Pageable pageable) {
        return this.userRepository.findLastUserCreated(pageable);
    }

    public List<Integer> findAdminIds() {
        return this.userRepository.findAdminIds();
    }

    public Page<User> findAllUsers(Pageable pageable, String query, List<UserStatus> statuses, List<RoleEnum> roles) {
        List<String> tokens = null;
        if(query != null && !query.isEmpty()){
            tokens = Arrays.stream(query.toLowerCase().split(" ")).toList();
        }

        Specification<User> specification =
                UserSpecifications.searchByTokens(tokens, statuses, roles);

        Page<User> users = this.userRepository.findAll(specification, pageable);


        return users;
    }
}
