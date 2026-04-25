# CODIGO DO FOOTER PRINCIPAL

# HTML

<footer>
    <div id="content-footer">
        <div id="footer-contatos">
            <p>royale concursos</p>
            <p>Contato</p>

            <div id="social-links">
                <a href="https://www.instagram.com/" class="footer-link" id="inta">
                    <i class="fa-brands fa-instagram"></i>
                </a>

                <a href="https://github.com/Juao-dev/royale-concursos.git" class="footer-link" id="github">
                    <i class="fa-brands fa-github"></i>
                </a>

                <a href="https://www.linkedin.com/feed/?trk=sem-ga_campid.12619604099_asid.149519181115_crid.725790844702_kw.linkedin_d.c_tid.kwd-148086543_n.g_mt.e_geo.9214449"
                    class="footer-link" id="linkedin">
                    <i class="fa-brands fa-linkedin"></i>
                </a>

                <a href="#" class="footer-link" id="zapp">
                    <i class="fa-brands fa-whatsapp"></i>
                </a>

                <a href="https://youtu.be/8rVIacbAlRg?si=LJ3l8UjpznA469_o" class="footer-link" id="tutube">
                    <i class="fa-brands fa-youtube"></i>
                </a>
            </div>
        </div>

        <ul class="footer-list">
            <li id="info">
                informaçao
            </li>

            <li>
                <a href="#" class="footer-links">Política de privacidade</a>
            </li>

            <li>
                <a href="#" class="footer-links">Termos de uso</a>
            </li>

            <li>
                <a href="#" class="footer-links">Política de cookies</a>
            </li>

            <li>
                <a href="#" class="footer-links">Suporte / Ajuda</a>
            </li>
        </ul>

        <div id="footer-sugestoes">
            <p id="sug">Sugestões</p>

            <p>Nos mande por email sugestoes e dicas para adições no nosso site.</p>

            <a href="https://mail.google.com/mail/u/1/#inbox?compose=new" id="email">royaleconcursosJPL@gmail.com</a>
        </div>
    </div>

    <div id="footer-copyright">
        &#169 2026 all rights reserved
    </div>

</footer>

# CSS

footer {
    margin-left: 80px;
    width: calc(100% - 80px);
    font-family: "DM Sans", sans-serif;
    margin-top: 110px;
}

#content-footer {
    background: linear-gradient(135deg, #04090e 0%, #1b263b 100%);
    color: white;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    padding: 3rem 3.5rem;
    gap: 2rem;
}

#footer-contatos p {
    font-size: 1.1rem;
    padding-bottom: 0.3rem;
    color: #a0aec0;
}

#footer-contatos p:first-child {
    font-size: 1.4rem;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 0.5rem;
}

#social-links {
    padding-top: 1.2rem;
    display: flex;
    gap: 0.8rem;
    flex-wrap: wrap;
}

#social-links .footer-link {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2.4rem;
    height: 2.4rem;
    border-radius: 50%;
    color: white;
    text-decoration: none;
    font-size: 1rem;
    transition: transform 0.2s ease, opacity 0.2s ease;
}

#social-links .footer-link:hover {
    transform: translateY(-3px);
    opacity: 0.85;
}

#inta   { background: linear-gradient(#7f37c9, #ff2992, #ff9807); }
#zapp   { background: linear-gradient(#01820e, #19ff57, #6fff1c); }
#linkedin { background: linear-gradient(#00065f, #034365, #030033); }
#github { background: #1a1a1a; border: 1px solid #333; }
#tutube { background: linear-gradient(#ff0000, #690808); }

.footer-list {
    list-style: none;
}

#info {
    font-size: 1.4rem;
    font-weight: 700;
    color: #ffffff;
    padding-bottom: 1rem;
}

.footer-links {
    text-decoration: none;
    font-size: 1rem;
    color: #a0aec0;
    display: block;
    padding: 3px 0;
    transition: color 0.2s ease;
}

.footer-links:hover {
    color: #ffffff;
}

#footer-sugestoes {
    font-size: 1rem;
    color: #a0aec0;
    line-height: 1.6;
}

#sug {
    font-size: 1.4rem;
    font-weight: 700;
    color: #ffffff;
    padding-bottom: 1rem;
}

#email {
    display: inline-block;
    margin-top: 8px;
    color: #93c5fd;
    text-decoration: underline;
    transition: color 0.2s ease;
}

#email:hover {
    color: #ffffff;
}

#footer-copyright {
    background: #07111c;
    color: #6b7280;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
}