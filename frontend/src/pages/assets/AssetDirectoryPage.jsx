import { useState } from "react";
import { Link } from "react-router-dom";

import {
    useAssets,
    useSearchAssets
} from "../../hooks/useAssets";

import AssetTable from "./AssetTable";

export default function AssetDirectoryPage() {

    const [page, setPage] = useState(0);
    const [search, setSearch] = useState("");

    // Always call both hooks (Rules of Hooks)
    const assetsQuery = useAssets(page, 10);

    const searchQuery = useSearchAssets(
        search,
        page,
        10
    );

    // Decide which data to display
    const query = search.trim()
        ? searchQuery
        : assetsQuery;

    const { data, isLoading, isError } = query;

    if (isLoading) {
        return <h2>Loading Assets...</h2>;
    }

    if (isError) {
        return <h2>Unable to load assets.</h2>;
    }

    return (

        <div className="page">

            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: "20px"
                }}
            >

                <h2>Assets</h2>

                <Link
                    to="/assets/new"
                    className="btn btn-primary"
                >
                    Register Asset
                </Link>

            </div>

            <input
                type="text"
                placeholder="Search by Asset Name, Tag or Serial Number..."
                value={search}
                onChange={(e) => {

                    setSearch(e.target.value);
                    setPage(0);

                }}
                style={{
                    width: "350px",
                    padding: "10px",
                    marginBottom: "20px"
                }}
            />

            <AssetTable
                assets={data?.content ?? []}
            />

            <div
                style={{
                    marginTop: "20px",
                    display: "flex",
                    justifyContent: "center",
                    gap: "10px"
                }}
            >

                <button
                    disabled={page === 0}
                    onClick={() => setPage(previous => previous - 1)}
                >
                    Previous
                </button>

                <span>

                    Page {page + 1}

                </span>

                <button
                    disabled={data?.last ?? true}
                    onClick={() => setPage(previous => previous + 1)}
                >
                    Next
                </button>

            </div>

        </div>

    );

}