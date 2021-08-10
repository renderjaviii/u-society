package usociety.manager.app.api;

public class TokenApiFixture {

    private static TokenApi defaultValue;

    public static String refreshToken = "REFRESH_TOKEN_VALUE";
    public static String accessToken = "ACCESS_TOKEN_VALUE";
    public static String tokenType = "TOKEN_TYPE_VALUE";
    public static String expiresIn = "EXPIRES_IN_VALUE";
    public static String scope = "SCOPE_VALUE";
    public static String jti = "JTI_VALUE";

    static {
        defaultValue = TokenApi.newBuilder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .scope(scope)
                .jti(jti)
                .build();
    }

    public static TokenApi value() {
        return defaultValue;
    }

}