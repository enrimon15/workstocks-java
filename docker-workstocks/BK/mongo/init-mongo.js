db.createUser(
    {
        user: "blog-user",
        pwd: "blog-password",
        roles: [
            {
                role: "readWrite",
                db: "blog"
            }
        ]
    }
)