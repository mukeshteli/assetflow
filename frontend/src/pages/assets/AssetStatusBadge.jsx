export default function AssetStatusBadge({ status }) {

    let background = "#6c757d";

    switch (status) {

        case "AVAILABLE":
            background = "#28a745";
            break;

        case "ALLOCATED":
            background = "#007bff";
            break;

        case "UNDER_MAINTENANCE":
            background = "#ffc107";
            break;

        case "RETIRED":
            background = "#343a40";
            break;

        case "LOST":
            background = "#dc3545";
            break;

        default:
            background = "#6c757d";

    }

    return (

        <span
            style={{
                background,
                color: "#fff",
                padding: "4px 10px",
                borderRadius: "15px",
                fontSize: "12px"
            }}
        >

            {status}

        </span>

    );

}