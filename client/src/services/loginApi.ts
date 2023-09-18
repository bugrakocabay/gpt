export const login = async (username: string, password: string) => {
    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });
        return response;
    } catch (error) {
        console.log(error);
        return null;
    }
};
