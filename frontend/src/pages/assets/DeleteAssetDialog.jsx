export default function DeleteAssetDialog({

                                              open,

                                              assetName,

                                              onCancel,

                                              onDelete

                                          }) {

    if (!open) {

        return null;

    }

    return (

        <div
            style={{

                position: "fixed",

                inset: 0,

                background: "rgba(0,0,0,.4)",

                display: "flex",

                justifyContent: "center",

                alignItems: "center",

                zIndex: 999

            }}
        >

            <div
                style={{

                    background: "#fff",

                    padding: "25px",

                    width: "420px",

                    borderRadius: "8px"

                }}
            >

                <h2>Delete Asset</h2>

                <p>

                    Are you sure you want to delete

                    <b> {assetName} </b>

                    ?

                </p>

                <p
                    style={{
                        color: "red"
                    }}
                >

                    This action cannot be undone.

                </p>

                <div
                    style={{

                        display: "flex",

                        justifyContent: "flex-end",

                        gap: "10px"

                    }}
                >

                    <button onClick={onCancel}>

                        Cancel

                    </button>

                    <button
                        style={{
                            background: "#dc3545",
                            color: "#fff"
                        }}
                        onClick={onDelete}
                    >

                        Delete

                    </button>

                </div>

            </div>

        </div>

    );

}