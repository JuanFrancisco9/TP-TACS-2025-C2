import React from 'react';
import FormularioCrearEvento from "../components/form/FormularioCrearEvento.tsx";
import {Container, CssBaseline} from "@mui/material";

const CreateEventPage: React.FC = () => {
    return (
        <>
            <CssBaseline />
            <Container maxWidth="md" sx={{ py: 4 }}>
                <FormularioCrearEvento />
            </Container>
        </>
    );
};

export default CreateEventPage;

