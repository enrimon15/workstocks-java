rsconf = {
    _id: "replica-set",
    members: [
        {_id: 0, host: "mongodbrs:27017"}
    ]
}

rs.initiate(rsconf);

rs.conf();
