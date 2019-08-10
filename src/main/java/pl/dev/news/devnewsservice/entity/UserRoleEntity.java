package pl.dev.news.devnewsservice.entity;

public enum  UserRoleEntity {
    USER("USER"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    PUBLISHER("PUBLISHER");

    private final String value;

    UserRoleEntity(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static UserRoleEntity fromValue(final String text) {
        for (final UserRoleEntity role : UserRoleEntity.values()) {
            if (String.valueOf(role.value).equals(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}
