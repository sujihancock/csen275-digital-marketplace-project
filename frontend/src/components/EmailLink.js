import React from "react";

function EmailLink({ email }) {
    return (
        <a href={`mailto:${email}`}>{email}</a>
    )
}

export default EmailLink;

