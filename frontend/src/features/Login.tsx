import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import authService from '../services/authService';
import type {LoginRequest} from '../types/auth';

function Login() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!formData.username || !formData.password) {
            setError('Por favor completa todos los campos');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const usuario = await authService.login(formData);

            console.log(usuario)
            // Redirigir según el rol
            if (usuario.rol === 'ROLE_ADMIN') {
                navigate('/estadisticas');
            }
            if (usuario.rol === 'ROLE_ORGANIZER') {
                navigate('/organizador/eventos')
            }
            if (usuario.rol === 'ROLE_USER') {
                navigate('/eventos')
            }else {
                navigate('/');
            }

        } catch (err) {
            setError(err instanceof Error ? err.message : 'Error al iniciar sesión');
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="min-vh-100 d-flex align-items-center" style={{backgroundColor: '#f8f9fa'}}>
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-6 col-lg-4">
                        <div className="card border-0 shadow-lg" style={{borderRadius: '16px'}}>
                            <div className="card-body p-5">
                                {/* HEADER */}
                                <div className="text-center mb-4">
                                    <h2 className="fw-bold text-dark mb-2">
                                        🔐 Iniciar Sesión
                                    </h2>
                                    <p className="text-muted">
                                        Accede a tu cuenta para continuar
                                    </p>
                                </div>

                                {/* ERROR */}
                                {error && (
                                    <div className="alert alert-danger border-0 mb-4" style={{borderRadius: '12px'}}>
                                        <div className="d-flex align-items-center">
                                            <span className="me-2">⚠️</span>
                                            <small>{error}</small>
                                        </div>
                                    </div>
                                )}

                                {/* FORM */}
                                <form onSubmit={handleSubmit}>
                                    <div className="mb-3">
                                        <label htmlFor="username" className="form-label fw-semibold">
                                            Usuario
                                        </label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="username"
                                            name="username"
                                            value={formData.username}
                                            onChange={handleInputChange}
                                            placeholder="Ingresa tu usuario"
                                            style={{borderRadius: '8px'}}
                                            disabled={loading}
                                        />
                                    </div>

                                    <div className="mb-4">
                                        <label htmlFor="password" className="form-label fw-semibold">
                                            Contraseña
                                        </label>
                                        <input
                                            type="password"
                                            className="form-control"
                                            id="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleInputChange}
                                            placeholder="Ingresa tu contraseña"
                                            style={{borderRadius: '8px'}}
                                            disabled={loading}
                                        />
                                    </div>

                                    <button
                                        type="submit"
                                        className="btn btn-primary w-100 py-2 fw-semibold"
                                        style={{borderRadius: '8px'}}
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2"></span>
                                                Iniciando sesión...
                                            </>
                                        ) : (
                                            '🚀 Ingresar'
                                        )}
                                    </button>
                                </form>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;