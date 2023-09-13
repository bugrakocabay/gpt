export const register = async (username: string, password: string) => {
    try {
        const response = await fetch("http://localhost:8080/user/register", {
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
