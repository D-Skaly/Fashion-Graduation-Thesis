package com.skaly.fashion_backend.common.infrastructure.security;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionService {

    private final Map<String, Role> roles = new HashMap<>();
    private final Map<String, Permission> permissions = new HashMap<>();

    public PermissionService() {
        initializeDefaultRolesAndPermissions();
    }

    private void initializeDefaultRolesAndPermissions() {
        // Define permissions
        Permission readProduct = new Permission("product:read", "Read product", "product", "read");
        Permission writeProduct = new Permission("product:write", "Write product", "product", "write");
        Permission deleteProduct = new Permission("product:delete", "Delete product", "product", "delete");
        Permission manageProduct = new Permission("product:manage", "Manage product", "product", "manage");

        Permission readOrder = new Permission("order:read", "Read order", "order", "read");
        Permission writeOrder = new Permission("order:write", "Write order", "order", "write");
        Permission deleteOrder = new Permission("order:delete", "Delete order", "order", "delete");
        Permission manageOrder = new Permission("order:manage", "Manage order", "order", "manage");

        Permission readUser = new Permission("user:read", "Read user", "user", "read");
        Permission writeUser = new Permission("user:write", "Write user", "user", "write");
        Permission deleteUser = new Permission("user:delete", "Delete user", "user", "delete");
        Permission manageUser = new Permission("user:manage", "Manage user", "user", "manage");

        Permission readCategory = new Permission("category:read", "Read category", "category", "read");
        Permission writeCategory = new Permission("category:write", "Write category", "category", "write");
        Permission deleteCategory = new Permission("category:delete", "Delete category", "category", "delete");
        Permission manageCategory = new Permission("category:manage", "Manage category", "category", "manage");

        // Store permissions
        permissions.put(readProduct.getName(), readProduct);
        permissions.put(writeProduct.getName(), writeProduct);
        permissions.put(deleteProduct.getName(), deleteProduct);
        permissions.put(manageProduct.getName(), manageProduct);

        permissions.put(readOrder.getName(), readOrder);
        permissions.put(writeOrder.getName(), writeOrder);
        permissions.put(deleteOrder.getName(), deleteOrder);
        permissions.put(manageOrder.getName(), manageOrder);

        permissions.put(readUser.getName(), readUser);
        permissions.put(writeUser.getName(), writeUser);
        permissions.put(deleteUser.getName(), deleteUser);
        permissions.put(manageUser.getName(), manageUser);

        permissions.put(readCategory.getName(), readCategory);
        permissions.put(writeCategory.getName(), writeCategory);
        permissions.put(deleteCategory.getName(), deleteCategory);
        permissions.put(manageCategory.getName(), manageCategory);

        // Define roles
        Role adminRole = new Role("ADMIN", "Administrator with full access");
        adminRole.addPermission(readProduct);
        adminRole.addPermission(writeProduct);
        adminRole.addPermission(deleteProduct);
        adminRole.addPermission(manageProduct);
        adminRole.addPermission(readOrder);
        adminRole.addPermission(writeOrder);
        adminRole.addPermission(deleteOrder);
        adminRole.addPermission(manageOrder);
        adminRole.addPermission(readUser);
        adminRole.addPermission(writeUser);
        adminRole.addPermission(deleteUser);
        adminRole.addPermission(manageUser);
        adminRole.addPermission(readCategory);
        adminRole.addPermission(writeCategory);
        adminRole.addPermission(deleteCategory);
        adminRole.addPermission(manageCategory);

        Role customerRole = new Role("CUSTOMER", "Customer with limited access");
        customerRole.addPermission(readProduct);
        customerRole.addPermission(readOrder);
        customerRole.addPermission(writeOrder);

        Role staffRole = new Role("STAFF", "Staff with moderate access");
        staffRole.addPermission(readProduct);
        staffRole.addPermission(writeProduct);
        staffRole.addPermission(readOrder);
        staffRole.addPermission(writeOrder);
        staffRole.addPermission(readCategory);

        // Store roles
        roles.put(adminRole.getName(), adminRole);
        roles.put(customerRole.getName(), customerRole);
        roles.put(staffRole.getName(), staffRole);
    }

    public Role getRole(String roleName) {
        return roles.get(roleName);
    }

    public Permission getPermission(String permissionName) {
        return permissions.get(permissionName);
    }

    public boolean hasPermission(String roleName, String resource, String action) {
        Role role = roles.get(roleName);
        if (role == null) {
            return false;
        }
        return role.hasPermission(resource, action);
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        Role role = roles.get(roleName);
        if (role != null) {
            role.addPermission(permission);
        }
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        Role role = roles.get(roleName);
        if (role != null) {
            role.removePermission(permission);
        }
    }
}