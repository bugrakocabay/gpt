export const login = async (username: string, password: string) => {
    try {
        const response = await fetch("http://localhost:8080/user/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });
        return response.json();
    } catch (error) {
        console.log(error);
        return null;
    }
};
