import AssetRow from "./AssetRow";

export default function AssetTable({ assets }) {

    return (

        <table className="table">

            <thead>

            <tr>

                <th>Asset Tag</th>

                <th>Name</th>

                <th>Category</th>

                <th>Condition</th>

                <th>Status</th>

                <th>Actions</th>

            </tr>

            </thead>

            <tbody>

            {

                assets.length === 0

                    ? (

                        <tr>

                            <td
                                colSpan="6"
                                style={{
                                    textAlign: "center"
                                }}
                            >

                                No Assets Found

                            </td>

                        </tr>

                    )

                    : (

                        assets.map(asset => (

                            <AssetRow

                                key={asset.id}

                                asset={asset}

                            />

                        ))

                    )

            }

            </tbody>

        </table>

    );

}